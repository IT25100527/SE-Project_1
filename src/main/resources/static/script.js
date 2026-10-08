// ===================================================================
// Rx+ PHARMACORE — Supplier Management
// Talks to the Spring Boot backend running on the SAME origin
// (http://localhost:8080/api/suppliers) since this file is served
// as a static resource from the Spring Boot app itself.
// ===================================================================

const API_BASE = '/api/suppliers';

// ---- Element references ----
const tableBody = document.getElementById('supplierTableBody');

const viewModal = document.getElementById('supplierModal');
const closeModalBtn = document.getElementById('closeModalBtn');
const modalCloseBtn = document.getElementById('modalCloseBtn');
const modalEditBtn = document.getElementById('modalEditBtn');

const formModal = document.getElementById('formModal');
const formModalTitle = document.getElementById('formModalTitle');
const formModalSubtitle = document.getElementById('formModalSubtitle');
const supplierForm = document.getElementById('supplierForm');
const openAddModalBtn = document.getElementById('openAddModalBtn');
const closeFormModalBtn = document.getElementById('closeFormModalBtn');
const cancelFormBtn = document.getElementById('cancelFormBtn');
const saveSupplierBtn = document.getElementById('saveSupplierBtn');

const toast = document.getElementById('toast');

let currentlyViewedSupplier = null; // holds the supplier object shown in the View modal

// ---- Toast helper ----
function showToast(message, isError = false) {
  toast.textContent = message;
  toast.classList.toggle('error', isError);
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 2500);
}

// ---- Helpers ----
function getInitials(name) {
  if (!name) return '??';
  const parts = name.trim().split(/\s+/);
  const initials = parts.slice(0, 2).map(p => p[0]).join('');
  return initials.toUpperCase();
}

function statusBadgeHtml(status) {
  const isActive = (status || '').toUpperCase() === 'ACTIVE';
  const cls = isActive ? 'badge-green' : 'badge-gray';
  const label = isActive ? 'Active' : 'Inactive';
  return `<span class="badge ${cls}"><span class="dot"></span> ${label}</span>`;
}

function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');
}

// ===================================================================
// LOAD & RENDER SUPPLIERS
// ===================================================================
async function loadSuppliers() {
  try {
    const res = await fetch(API_BASE);
    if (!res.ok) throw new Error('Failed to load suppliers');
    const suppliers = await res.json();
    renderTable(suppliers);
  } catch (err) {
    console.error(err);
    tableBody.innerHTML = `<tr class="empty-row"><td colspan="6">
        Could not load suppliers. Is the backend running on port 8080?
      </td></tr>`;
    showToast('Could not connect to the server', true);
  }
}

function renderTable(suppliers) {
  if (!suppliers || suppliers.length === 0) {
    tableBody.innerHTML = `<tr class="empty-row"><td colspan="6">No suppliers yet. Click "+ Add Supplier" to create one.</td></tr>`;
    return;
  }

  tableBody.innerHTML = suppliers.map(s => `
    <tr data-id="${s.id}">
      <td class="font-bold">${escapeHtml(s.supplierName)}</td>
      <td>${escapeHtml(s.contactPerson)}</td>
      <td>
        <div>${escapeHtml(s.email)}</div>
        <div class="text-sub">${escapeHtml(s.phone)}</div>
      </td>
      <td><span class="badge badge-blue">${escapeHtml(s.category)}</span></td>
      <td>${statusBadgeHtml(s.status)}</td>
      <td>
        <div class="action-buttons">
          <button class="btn-action view-btn" data-id="${s.id}">View</button>
          <button class="btn-action edit-btn" data-id="${s.id}">Edit</button>
          <button class="btn-action btn-danger delete-btn" data-id="${s.id}">Delete</button>
        </div>
      </td>
    </tr>
  `).join('');

  // Wire up action buttons for this render pass
  tableBody.querySelectorAll('.view-btn').forEach(btn =>
      btn.addEventListener('click', () => openViewModal(btn.dataset.id)));
  tableBody.querySelectorAll('.edit-btn').forEach(btn =>
      btn.addEventListener('click', () => openEditModal(btn.dataset.id)));
  tableBody.querySelectorAll('.delete-btn').forEach(btn =>
      btn.addEventListener('click', () => deleteSupplier(btn.dataset.id)));
}

// ===================================================================
// VIEW MODAL
// ===================================================================
async function openViewModal(id) {
  try {
    const res = await fetch(`${API_BASE}/${id}`);
    if (!res.ok) throw new Error('Supplier not found');
    const s = await res.json();
    currentlyViewedSupplier = s;

    document.getElementById('modalAvatar').innerText = getInitials(s.supplierName);
    document.getElementById('modalSupplierName').innerText = s.supplierName || '-';
    document.getElementById('modalSupplierId').innerText = 'Supplier ID: ' + (s.supplierCode || s.id);
    document.getElementById('modalCategory').innerText = s.category || '-';

    const statusEl = document.getElementById('modalStatus');
    const isActive = (s.status || '').toUpperCase() === 'ACTIVE';
    statusEl.className = 'badge ' + (isActive ? 'badge-green' : 'badge-gray');
    statusEl.innerHTML = `<span class="dot"></span> ${isActive ? 'Active' : 'Inactive'}`;

    document.getElementById('modalContactPerson').innerText = s.contactPerson || '-';
    document.getElementById('modalDesignation').innerText = s.designation || '-';
    document.getElementById('modalPhone').innerText = s.phone || '-';
    document.getElementById('modalEmail').innerText = s.email || '-';
    document.getElementById('modalBrn').innerText = s.businessRegNo || '-';
    document.getElementById('modalPaymentTerms').innerText = s.paymentTerms || '-';
    document.getElementById('modalAddress').innerText = s.address || '-';

    viewModal.classList.add('active');
  } catch (err) {
    console.error(err);
    showToast('Could not load supplier details', true);
  }
}

function hideViewModal() {
  viewModal.classList.remove('active');
}

closeModalBtn.addEventListener('click', hideViewModal);
modalCloseBtn.addEventListener('click', hideViewModal);

// "Edit Details" inside the View modal -> jump straight into edit form
modalEditBtn.addEventListener('click', () => {
  if (!currentlyViewedSupplier) return;
  hideViewModal();
  openEditModal(currentlyViewedSupplier.id);
});

window.addEventListener('click', (event) => {
  if (event.target === viewModal) hideViewModal();
  if (event.target === formModal) hideFormModal();
});

// ===================================================================
// ADD / EDIT FORM MODAL
// ===================================================================
function resetForm() {
  supplierForm.reset();
  document.getElementById('supplierId').value = '';
  clearFieldErrors();
}

function openAddModal() {
  resetForm();
  formModalTitle.innerText = 'Add New Supplier';
  formModalSubtitle.innerText = 'Enter the details for a new pharmaceutical supplier partner.';
  saveSupplierBtn.innerText = 'Save Supplier';
  formModal.classList.add('active');
}

async function openEditModal(id) {
  try {
    const res = await fetch(`${API_BASE}/${id}`);
    if (!res.ok) throw new Error('Supplier not found');
    const s = await res.json();

    resetForm();
    formModalTitle.innerText = 'Edit Supplier';
    formModalSubtitle.innerText = 'Update the details for this supplier partner.';
    saveSupplierBtn.innerText = 'Update Supplier';

    document.getElementById('supplierId').value = s.id;
    document.getElementById('supplierName').value = s.supplierName || '';
    document.getElementById('businessRegNo').value = s.businessRegNo || '';
    document.getElementById('contactPerson').value = s.contactPerson || '';
    document.getElementById('designation').value = s.designation || '';
    document.getElementById('email').value = s.email || '';
    document.getElementById('phone').value = s.phone || '';
    document.getElementById('category').value = s.category || '';
    document.getElementById('paymentTerms').value = s.paymentTerms || '';
    document.getElementById('address').value = s.address || '';

    const statusValue = (s.status || 'ACTIVE').toUpperCase();
    const radio = supplierForm.querySelector(`input[name="status"][value="${statusValue}"]`);
    if (radio) radio.checked = true;

    formModal.classList.add('active');
  } catch (err) {
    console.error(err);
    showToast('Could not load supplier for editing', true);
  }
}

function hideFormModal() {
  formModal.classList.remove('active');
  resetForm();
}

openAddModalBtn.addEventListener('click', openAddModal);
closeFormModalBtn.addEventListener('click', hideFormModal);
cancelFormBtn.addEventListener('click', hideFormModal);

function clearFieldErrors() {
  supplierForm.querySelectorAll('.field-error').forEach(el => el.remove());
}

function showValidationErrors(errors) {
  clearFieldErrors();
  Object.entries(errors).forEach(([field, message]) => {
    const input = document.getElementById(field);
    if (!input) return;
    const errEl = document.createElement('div');
    errEl.className = 'field-error';
    errEl.innerText = message;
    input.parentElement.appendChild(errEl);
  });
}

supplierForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  clearFieldErrors();

  const id = document.getElementById('supplierId').value;
  const statusRadio = supplierForm.querySelector('input[name="status"]:checked');

  const payload = {
    supplierName: document.getElementById('supplierName').value.trim(),
    contactPerson: document.getElementById('contactPerson').value.trim(),
    designation: document.getElementById('designation').value.trim(),
    email: document.getElementById('email').value.trim(),
    phone: document.getElementById('phone').value.trim(),
    category: document.getElementById('category').value,
    businessRegNo: document.getElementById('businessRegNo').value.trim(),
    paymentTerms: document.getElementById('paymentTerms').value.trim(),
    address: document.getElementById('address').value.trim(),
    status: statusRadio ? statusRadio.value : 'ACTIVE'
  };

  saveSupplierBtn.disabled = true;
  const originalLabel = saveSupplierBtn.innerText;
  saveSupplierBtn.innerText = 'Saving...';

  try {
    const isEdit = !!id;
    const url = isEdit ? `${API_BASE}/${id}` : API_BASE;
    const method = isEdit ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (res.status === 400) {
      const errors = await res.json();
      showValidationErrors(errors);
      showToast('Please fix the highlighted fields', true);
      return;
    }

    if (!res.ok) throw new Error('Save failed');

    showToast(isEdit ? 'Supplier updated successfully' : 'Supplier added successfully');
    hideFormModal();
    loadSuppliers();
  } catch (err) {
    console.error(err);
    showToast('Something went wrong while saving. Please try again.', true);
  } finally {
    saveSupplierBtn.disabled = false;
    saveSupplierBtn.innerText = originalLabel;
  }
});

// ===================================================================
// DELETE
// ===================================================================
async function deleteSupplier(id) {
  if (!confirm('Are you sure you want to delete this supplier? This cannot be undone.')) return;

  try {
    const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Delete failed');
    showToast('Supplier deleted');
    loadSuppliers();
  } catch (err) {
    console.error(err);
    showToast('Could not delete supplier', true);
  }
}

// ===================================================================
// INIT
// ===================================================================
document.addEventListener('DOMContentLoaded', loadSuppliers);
