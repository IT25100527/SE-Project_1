/* Pharmacy Management System - Sales & Billing front end (plain JS, talks to /api) */
const API = '/api';
const $ = id => document.getElementById(id);
const r2 = n => Math.round((Number(n) || 0) * 100) / 100;
const money = n => 'Rs. ' + r2(n).toLocaleString('en-LK', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const fmtDate = d => d ? new Date(d).toLocaleString('en-GB', { dateStyle: 'medium', timeStyle: 'short' }) : '';
const todayStr = () => { const d = new Date(); return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0'); };
const isExpired = m => m.expiryDate < todayStr();

let medicines = [], cart = [], invoices = [], paidTouched = false, toastTimer;

async function api(path, options = {}) {
  const res = await fetch(API + path, { headers: { 'Content-Type': 'application/json' }, ...options });
  const data = res.status === 204 ? null : await res.json().catch(() => null);
  if (!res.ok) throw new Error((data && data.message) || 'Request failed (' + res.status + ')');
  return data;
}

function toast(msg, isError = false) {
  const t = $('toast');
  t.textContent = msg;
  t.className = 'show' + (isError ? ' error' : '');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => t.className = '', 3500);
}

/* ---------- tabs ---------- */
document.querySelectorAll('.tab').forEach(b => b.addEventListener('click', () => showTab(b.dataset.tab)));
function showTab(name) {
  document.querySelectorAll('.tab').forEach(b => b.classList.toggle('active', b.dataset.tab === name));
  document.querySelectorAll('.view').forEach(v => v.hidden = v.id !== 'view-' + name);
  if (name === 'invoices') loadInvoices();
  if (name === 'medicines' || name === 'sale') loadMedicines();
}

/* ---------- medicines (shared by sale picker + medicines tab) ---------- */
async function loadMedicines() {
  try {
    medicines = await api('/medicines');
    renderSaleMedicines();
    renderMedicines();
  } catch (e) { toast(e.message, true); }
}

function stockCell(m) {
  if (m.quantityInStock <= 0) return '<span class="tag-bad">Out of stock</span>';
  if (m.quantityInStock <= m.reorderLevel) return `<span class="tag-low" title="At or below reorder level">${m.quantityInStock}</span>`;
  return m.quantityInStock;
}
const expiryCell = m => (isExpired(m) ? '<span class="tag-bad">Expired</span> ' : '') + esc(m.expiryDate);

function renderSaleMedicines() {
  const q = $('saleSearch').value.trim().toLowerCase();
  const rows = medicines.filter(m => m.name.toLowerCase().includes(q));
  $('saleMedicines').innerHTML = rows.length ? rows.map(m => {
    const off = isExpired(m) || m.quantityInStock <= 0;
    return `<tr class="${off ? 'disabled' : ''}">
      <td class="name">${esc(m.name)}</td><td>${esc(m.batchNo)}</td>
      <td class="num">${money(m.unitPrice)}</td><td class="num">${stockCell(m)}</td><td>${expiryCell(m)}</td>
      <td class="num"><input type="number" min="1" value="1" id="q-${m.id}" ${off ? 'disabled' : ''} aria-label="Quantity of ${esc(m.name)}"></td>
      <td><button class="ghost" ${off ? 'disabled' : ''} onclick="addToCart(${m.id})">Add</button></td></tr>`;
  }).join('') : '<tr><td class="empty" colspan="7">No medicines found. Add medicines in the Medicines tab.</td></tr>';
}

function renderMedicines() {
  const q = $('medSearch').value.trim().toLowerCase();
  const rows = medicines.filter(m => m.name.toLowerCase().includes(q));
  $('medBody').innerHTML = rows.length ? rows.map(m => `<tr>
      <td class="name">${esc(m.name)}</td><td>${esc(m.category)}</td><td>${esc(m.batchNo)}</td>
      <td class="num">${money(m.unitPrice)}</td><td class="num">${stockCell(m)}</td><td>${expiryCell(m)}</td>
      <td><button class="link" onclick="editMedicine(${m.id})">Edit</button>
          <button class="link danger" onclick="deleteMedicine(${m.id})">Delete</button></td></tr>`).join('')
    : '<tr><td class="empty" colspan="7">No medicines yet. Use the form to add the first one.</td></tr>';
}

$('saleSearch').addEventListener('input', renderSaleMedicines);
$('medSearch').addEventListener('input', renderMedicines);

function resetMedForm() {
  $('medForm').reset();
  $('medId').value = '';
  $('medReorder').value = 10;
  $('medFormTitle').textContent = 'Add medicine';
  $('medCancel').hidden = true;
}
function editMedicine(id) {
  const m = medicines.find(x => x.id === id);
  $('medId').value = m.id; $('medName').value = m.name; $('medCategory').value = m.category || '';
  $('medBatch').value = m.batchNo || ''; $('medPrice').value = m.unitPrice; $('medStock').value = m.quantityInStock;
  $('medExpiry').value = m.expiryDate; $('medReorder').value = m.reorderLevel;
  $('medFormTitle').textContent = 'Edit medicine';
  $('medCancel').hidden = false;
  $('medName').focus();
}
async function deleteMedicine(id) {
  const m = medicines.find(x => x.id === id);
  if (!confirm(`Delete ${m.name}? Past invoices keep their records.`)) return;
  try { await api('/medicines/' + id, { method: 'DELETE' }); toast('Medicine deleted'); loadMedicines(); }
  catch (e) { toast(e.message, true); }
}
$('medCancel').addEventListener('click', resetMedForm);
$('medForm').addEventListener('submit', async ev => {
  ev.preventDefault();
  const id = $('medId').value;
  const body = {
    name: $('medName').value.trim(), category: $('medCategory').value.trim(), batchNo: $('medBatch').value.trim(),
    unitPrice: Number($('medPrice').value), quantityInStock: parseInt($('medStock').value, 10),
    expiryDate: $('medExpiry').value, reorderLevel: parseInt($('medReorder').value, 10) || 0
  };
  try {
    await api(id ? '/medicines/' + id : '/medicines', { method: id ? 'PUT' : 'POST', body: JSON.stringify(body) });
    toast(id ? 'Medicine updated' : 'Medicine added');
    resetMedForm();
    loadMedicines();
  } catch (e) { toast(e.message, true); }
});

/* ---------- new sale / bill ---------- */
function addToCart(id) {
  const m = medicines.find(x => x.id === id);
  const qty = parseInt($('q-' + id).value, 10) || 0;
  if (qty < 1) return toast('Enter a quantity of at least 1', true);
  const line = cart.find(c => c.id === id);
  if ((line ? line.qty : 0) + qty > m.quantityInStock) return toast(`Only ${m.quantityInStock} of ${m.name} in stock`, true);
  if (line) line.qty += qty;
  else cart.push({ id, name: m.name, price: Number(m.unitPrice), qty, max: m.quantityInStock });
  $('q-' + id).value = 1;
  renderCart();
}
function setQty(id, v) {
  const line = cart.find(c => c.id === id);
  let qty = parseInt(v, 10);
  if (!qty || qty < 1) qty = 1;
  if (qty > line.max) { toast(`Only ${line.max} of ${line.name} in stock`, true); qty = line.max; }
  line.qty = qty;
  renderCart();
}
function removeFromCart(id) { cart = cart.filter(c => c.id !== id); renderCart(); }

function billNumbers() {
  const subtotal = r2(cart.reduce((s, c) => s + c.price * c.qty, 0));
  const discount = Math.min(r2($('discount').value), subtotal);
  const total = r2(subtotal - discount);
  const paid = r2($('amountPaid').value);
  return { subtotal, discount, total, paid };
}

function renderCart() {
  $('cartBody').innerHTML = cart.length ? cart.map(c => `<tr>
      <td class="name">${esc(c.name)}<br><small>${money(c.price)} each</small></td>
      <td class="num"><input type="number" min="1" max="${c.max}" value="${c.qty}" onchange="setQty(${c.id}, this.value)" aria-label="Quantity of ${esc(c.name)}"></td>
      <td class="num">${money(c.price * c.qty)}</td>
      <td><button class="link danger" onclick="removeFromCart(${c.id})">Remove</button></td></tr>`).join('')
    : '<tr><td class="empty" colspan="4">No items yet. Add medicines from the list.</td></tr>';
  updateTotals();
}

function updateTotals() {
  const first = billNumbers();
  if (!paidTouched) $('amountPaid').value = first.total.toFixed(2);   // default: customer pays in full
  const { subtotal, total, paid } = billNumbers();
  $('tSubtotal').textContent = money(subtotal);
  $('tTotal').textContent = money(total);
  const note = $('payNote');
  note.className = 'note';
  if (!cart.length) note.textContent = '';
  else if (paid > total) note.textContent = `Change to return: ${money(paid - total)}`;
  else if (paid < total) { note.className = 'note warn'; note.textContent = `${money(total - paid)} will stay as the balance on this invoice.`; }
  else note.textContent = 'Fully paid.';
}
$('discount').addEventListener('input', updateTotals);
$('amountPaid').addEventListener('input', () => { paidTouched = true; updateTotals(); });

function resetSale() {
  cart = []; paidTouched = false;
  $('custName').value = ''; $('custPhone').value = ''; $('discount').value = 0; $('payMethod').value = 'CASH';
  renderCart();
}

$('completeSale').addEventListener('click', async () => {
  if (!cart.length) return toast('Add at least one medicine to the bill', true);
  const { discount, paid } = billNumbers();
  const btn = $('completeSale');
  btn.disabled = true;
  try {
    const inv = await api('/invoices', {
      method: 'POST',
      body: JSON.stringify({
        customerName: $('custName').value.trim(), customerPhone: $('custPhone').value.trim(),
        items: cart.map(c => ({ medicineId: c.id, quantity: c.qty })),
        discount, amountPaid: paid, paymentMethod: $('payMethod').value
      })
    });
    const change = r2(Math.max(0, paid - inv.totalAmount));
    resetSale();
    await loadMedicines();            // stock has changed
    showInvoice(inv, change);
    toast('Sale completed: ' + inv.invoiceNumber);
  } catch (e) {
    toast(e.message, true);
    loadMedicines();                  // refresh stock figures after a failed sale
  } finally { btn.disabled = false; }
});

/* ---------- invoices ---------- */
async function loadInvoices() {
  try { invoices = await api('/invoices'); renderInvoices(); }
  catch (e) { toast(e.message, true); }
}
function renderInvoices() {
  const q = $('invoiceSearch').value.trim().toLowerCase();
  const rows = invoices.filter(i => (i.invoiceNumber + ' ' + i.customerName).toLowerCase().includes(q));
  $('invoiceBody').innerHTML = rows.length ? rows.map(i => `<tr>
      <td>${esc(i.invoiceNumber)}</td><td>${fmtDate(i.invoiceDate)}</td><td class="name">${esc(i.customerName)}</td>
      <td class="num">${money(i.totalAmount)}</td><td class="num">${money(i.paidAmount)}</td><td class="num">${money(i.balance)}</td>
      <td><span class="badge ${i.paymentStatus}">${i.paymentStatus}</span></td>
      <td><button class="link" onclick="openInvoice(${i.id})">${Number(i.balance) > 0 ? 'View / add payment' : 'View'}</button></td></tr>`).join('')
    : '<tr><td class="empty" colspan="8">No invoices yet. Completed sales appear here.</td></tr>';
}
$('invoiceSearch').addEventListener('input', renderInvoices);

function openInvoice(id) { showInvoice(invoices.find(i => i.id === id)); }

function showInvoice(inv, change = 0) {
  const items = inv.items.map(it => `<tr><td class="name">${esc(it.medicineName)}</td><td>${esc(it.batchNo)}</td>
      <td class="num">${it.quantity}</td><td class="num">${money(it.unitPrice)}</td><td class="num">${money(it.lineTotal)}</td></tr>`).join('');
  const pays = inv.payments.length ? `<h3 style="font-size:.95rem;margin:1rem 0 .3rem">Payments received</h3>
      <table><tbody>${inv.payments.map(p => `<tr><td>${fmtDate(p.paidAt)}</td><td>${esc(p.method)}</td><td class="num">${money(p.amount)}</td></tr>`).join('')}</tbody></table>` : '';
  $('invoiceBodyView').innerHTML = `
    <div class="inv-head">
      <div><h2>Invoice ${esc(inv.invoiceNumber)}</h2><div class="inv-meta">${fmtDate(inv.invoiceDate)}</div></div>
      <div class="inv-meta" style="text-align:right">Customer<br><strong>${esc(inv.customerName)}</strong><br>${esc(inv.customerPhone)}</div>
    </div>
    <div class="table-wrap"><table>
      <thead><tr><th>Medicine</th><th>Batch</th><th class="num">Qty</th><th class="num">Unit price</th><th class="num">Total</th></tr></thead>
      <tbody>${items}</tbody></table></div>
    <div class="inv-totals">
      <div><span>Subtotal</span><span>${money(inv.subtotal)}</span></div>
      <div><span>Discount</span><span>- ${money(inv.discount)}</span></div>
      <div class="grand"><span>Total</span><span>${money(inv.totalAmount)}</span></div>
      <div><span>Paid</span><span>${money(inv.paidAmount)}</span></div>
      <div><span>Balance due</span><span>${money(inv.balance)}</span></div>
      ${change > 0 ? `<div><span>Change returned</span><span>${money(change)}</span></div>` : ''}
      <div><span>Status</span><span class="badge ${inv.paymentStatus}">${inv.paymentStatus}</span></div>
    </div>${pays}`;

  $('payBox').innerHTML = Number(inv.balance) > 0 ? `<div class="pay-box">
      <h3 style="font-size:.95rem;margin:0 0 .5rem">Record a payment</h3>
      <div class="field-row">
        <label>Amount (Rs.)<input id="payAmount" type="number" min="0.01" max="${inv.balance}" step="0.01" value="${inv.balance}"></label>
        <label>Method<select id="payMethod2"><option>CASH</option><option>CARD</option><option>ONLINE</option></select></label>
      </div>
      <button class="primary" onclick="recordPayment(${inv.id})">Record payment</button></div>` : '';
  const dlg = $('invoiceDialog');
  if (!dlg.open) dlg.showModal();
}

async function recordPayment(id) {
  try {
    const inv = await api(`/invoices/${id}/payments`, {
      method: 'POST',
      body: JSON.stringify({ amount: r2($('payAmount').value), method: $('payMethod2').value })
    });
    toast('Payment recorded');
    showInvoice(inv);
    loadInvoices();
  } catch (e) { toast(e.message, true); }
}

/* ---------- start ---------- */
renderCart();
loadMedicines();
