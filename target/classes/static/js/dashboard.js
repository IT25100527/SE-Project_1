// Renders one dashboard shell for every role. ADMIN and PHARMACY_OWNER get a live,
// functional data view here (user management, this module's job); other roles
// show their nav + placeholder stat cards, since those modules belong to teammates.

const ROLE_META = {
  ADMIN: {
    label: 'Administrator',
    eyebrow: 'ADMIN CONSOLE',
    title: 'Administrator Dashboard',
    sub: 'Manage users, roles, permissions and oversee every module.',
    nav: [
      { icon: 'US', label: 'User Management', key: 'users' },
      { icon: 'RL', label: 'Roles & Permissions', key: 'roles' },
      { icon: 'IN', label: 'Inventory', key: 'inventory' },
      { icon: 'OR', label: 'Orders', key: 'orders' },
      { icon: 'SY', label: 'System Settings', key: 'settings' }
    ]
  },
  PHARMACY_OWNER: {
    label: 'Pharmacy Owner',
    eyebrow: 'OWNER CONSOLE',
    title: 'Pharmacy Owner Dashboard',
    sub: 'Full visibility across modules, staff and financials.',
    nav: [
      { icon: 'US', label: 'Admins & Staff', key: 'users' },
      { icon: 'RL', label: 'Roles & Permissions', key: 'roles' },
      { icon: 'RP', label: 'Reports', key: 'reports' },
      { icon: 'IN', label: 'Inventory', key: 'inventory' },
      { icon: 'OR', label: 'Orders', key: 'orders' }
    ]
  },
  INVENTORY_MANAGER: {
    label: 'Inventory Manager',
    eyebrow: 'INVENTORY',
    title: 'Inventory Manager Dashboard',
    sub: 'Monitor stock levels, expiry dates and medicine availability.',
    nav: [
      { icon: 'IN', label: 'Stock Levels', key: 'stock' },
      { icon: 'EX', label: 'Expiry Tracking', key: 'expiry' },
      { icon: 'RC', label: 'Stock Receipts', key: 'receipts' }
    ]
  },
  DELIVERY_STAFF: {
    label: 'Delivery Staff',
    eyebrow: 'DELIVERIES',
    title: 'Delivery Dashboard',
    sub: 'Track and update your assigned deliveries.',
    nav: [
      { icon: 'DL', label: 'My Deliveries', key: 'deliveries' },
      { icon: 'HS', label: 'Delivery History', key: 'history' }
    ]
  },
  PHARMACIST: {
    label: 'Pharmacist',
    eyebrow: 'PHARMACY',
    title: 'Pharmacist Dashboard',
    sub: 'Dispense medicines, manage prescriptions and billing.',
    nav: [
      { icon: 'PR', label: 'Prescriptions', key: 'prescriptions' },
      { icon: 'SL', label: 'Sales & Billing', key: 'sales' },
      { icon: 'IN', label: 'Inventory Check', key: 'inventory' }
    ]
  },
  CASHIER: {
    label: 'Cashier',
    eyebrow: 'CHECKOUT',
    title: 'Cashier Dashboard',
    sub: 'Process sales, generate bills and accept payments.',
    nav: [
      { icon: 'SL', label: 'New Sale', key: 'sale' },
      { icon: 'RC', label: 'Receipts', key: 'receipts' }
    ]
  },
  PURCHASE_MANAGER: {
    label: 'Purchase Manager',
    eyebrow: 'PURCHASING',
    title: 'Purchase Manager Dashboard',
    sub: 'Create purchase orders and coordinate with suppliers.',
    nav: [
      { icon: 'PO', label: 'Purchase Orders', key: 'orders' },
      { icon: 'SU', label: 'Suppliers', key: 'suppliers' }
    ]
  },
  CUSTOMER: {
    label: 'Customer',
    eyebrow: 'MY ACCOUNT',
    title: 'Your Dashboard',
    sub: 'Order medicines and track your deliveries and prescriptions.',
    nav: [
      { icon: 'AC', label: 'My Account', key: 'account' },
      { icon: 'SH', label: 'Shop Medicines', key: 'shop' },
      { icon: 'OR', label: 'My Orders', key: 'orders' },
      { icon: 'PR', label: 'My Prescriptions', key: 'prescriptions' }
    ]
  }
};

let currentRole = null;
let lastLoadedUsers = {};
let activeUserTab = 'staff';

async function init() {
  let session;
  try {
    session = (await getJson('/api/session')).data;
  } catch (e) {
    window.location.href = '/manager/login.html';
    return;
  }

  if (!session.role) {
    window.location.href = '/manager/login.html';
    return;
  }

  currentRole = session.role;
  const meta = ROLE_META[currentRole] || ROLE_META.CUSTOMER;

  document.getElementById('whoName').textContent = session.name || 'User';
  document.getElementById('whoRole').textContent = meta.label;
  document.getElementById('avatarInitial').textContent = (session.name || 'U').trim().charAt(0).toUpperCase();
  document.getElementById('eyebrow').textContent = meta.eyebrow;
  document.getElementById('pageTitle').textContent = meta.title;
  document.getElementById('pageSub').textContent = meta.sub;

  renderNav(meta.nav);
  renderStats(currentRole);

  if (currentRole === 'ADMIN' || currentRole === 'PHARMACY_OWNER') {
    renderAdminUserManagement();
  } else if (currentRole === 'CUSTOMER') {
    renderMyAccount();
  } else {
    renderPlaceholder(meta.nav[0].label);
  }

  document.getElementById('logoutLink').addEventListener('click', logout);
}

function renderNav(items) {

  const navList = document.getElementById('navList');

  navList.innerHTML = items.map((item, idx) => `
        <li
            class="nav-item ${idx === 0 ? 'active' : ''}"
            data-key="${item.key}"
            style="cursor:pointer;"
        >
            <span class="tab-icon">${item.icon}</span>
            ${item.label}
        </li>
    `).join('');


  navList.querySelectorAll('.nav-item').forEach(item => {

    item.addEventListener('click', function () {

      const key = this.dataset.key;


      // Active menu item
      navList.querySelectorAll('.nav-item')
          .forEach(nav => nav.classList.remove('active'));

      this.classList.add('active');


      // -------------------------
      // User Management
      // -------------------------

      if (key === 'users') {

        if (
            currentRole === 'ADMIN' ||
            currentRole === 'PHARMACY_OWNER'
        ) {

          renderAdminUserManagement();

        }

        return;
      }


      // -------------------------
      // Roles & Permissions
      // -------------------------

      if (key === 'roles') {

        if (
            currentRole === 'ADMIN' ||
            currentRole === 'PHARMACY_OWNER'
        ) {

          renderRolesPermissions();

        }

        return;
      }


      // -------------------------
      // Other modules
      // -------------------------

      renderPlaceholder(
          this.textContent.trim()
      );

    });

  });
}
function readRole(id) {

  alert(
      'Role ID: ' + id +
      '\n\nRole details and permissions will be displayed here.'
  );

}
async function deleteRole(id) {

  if (
      currentRole !== 'ADMIN' &&
      currentRole !== 'PHARMACY_OWNER'
  ) {

    showToast(
        'You do not have permission to delete roles.',
        'error'
    );

    return;
  }


  if (
      !confirm(
          'Are you sure you want to delete this role?'
      )
  ) {
    return;
  }


  try {

    const res = await fetch(
        `/api/admin/roles/${id}`,
        {
          method: 'DELETE',
          credentials: 'same-origin'
        }
    );


    const data = await res.json();


    if (
        !res.ok ||
        data.success === false
    ) {

      throw new Error(
          data.message ||
          'Could not delete role'
      );

    }


    showToast(
        'Role deleted successfully.',
        'success'
    );


    await loadRoles();


  } catch (e) {

    showToast(
        e.message ||
        'Delete failed',
        'error'
    );

  }

}
function renderStats(role) {
  const statSets = {
    ADMIN: [
      ['Total Users', '—', 'Across all roles'],
      ['Active Staff', '—', 'Currently active'],
      ['Customers', '—', 'Registered customers'],
      ['Unverified Emails', '—', 'Pending verification']
    ],
    PHARMACY_OWNER: [
      ['Total Users', '—', 'Across all roles'],
      ['Active Staff', '—', 'Currently active'],
      ['Customers', '—', 'Registered customers'],
      ['Unverified Emails', '—', 'Pending verification']
    ],
    INVENTORY_MANAGER: [['Low Stock Items', '—', ''], ['Expiring Soon', '—', ''], ['Total SKUs', '—', '']],
    DELIVERY_STAFF: [['Pending Deliveries', '—', ''], ['Completed Today', '—', '']],
    PHARMACIST: [['Pending Prescriptions', '—', ''], ["Today's Sales", '—', '']],
    CASHIER: [["Today's Bills", '—', ''], ['Total Collected', '—', '']],
    PURCHASE_MANAGER: [['Open Purchase Orders', '—', ''], ['Suppliers', '—', '']],
    CUSTOMER: [['Active Orders', '—', ''], ['Prescriptions on File', '—', '']]
  };
  const set = statSets[role] || [];
  document.getElementById('statGrid').innerHTML = set.map(([label, value, sub]) => `
    <div class="stat-card">
      <div class="stat-label">${label}</div>
      <div class="stat-value">${value}</div>
      <div class="stat-sub">${sub}</div>
    </div>
  `).join('');
}

function renderPlaceholder(sectionLabel) {
  document.getElementById('contentArea').innerHTML = `
    <div class="card card-pad empty-state">
      <div class="glyph">＋</div>
      <p><strong>${sectionLabel}</strong> lives in this module once it's wired up by the module owner.</p>
      <p>This dashboard shell, your session and role-based access already work end-to-end.</p>
    </div>
  `;
}

// ---------------- Admin / Pharmacy Owner: User Management ----------------
// ---------------- Roles & Permissions ----------------

async function renderRolesPermissions() {

  console.log("Loading Roles & Permissions...");

  document.getElementById('pageActions').innerHTML = '';

  document.getElementById('contentArea').innerHTML = `
        <div class="card">

            <div style="padding:24px 24px 18px;">
                <h2 style="
                    font-family:var(--font-display);
                    margin:0 0 8px;
                ">
                    Roles & Permissions
                </h2>

                <p style="
                    color:var(--ink-muted);
                    margin:0;
                ">
                    View and manage system roles and permissions.
                </p>
            </div>

            <div class="table-wrap">

                <table>

                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>ROLE</th>
                            <th>DESCRIPTION</th>
                            <th>ACTIONS</th>
                        </tr>
                    </thead>

                    <tbody id="rolesTableBody">

                        <tr>
                            <td
                                colspan="4"
                                style="
                                    text-align:center;
                                    padding:30px;
                                "
                            >
                                Loading roles...
                            </td>
                        </tr>

                    </tbody>

                </table>

            </div>

        </div>
    `;

  await loadRoles();
}

async function loadRoles() {

  try {

    console.log("Loading roles from API...");

    const response =
        await getJson('/api/admin/roles');

    console.log("Roles API response:", response);

    const roles = response.data || [];

    const tbody =
        document.getElementById('rolesTableBody');

    if (!tbody) {

      console.error(
          "rolesTableBody not found"
      );

      return;
    }

    if (roles.length === 0) {

      tbody.innerHTML = `
                <tr>
                    <td
                        colspan="4"
                        style="
                            text-align:center;
                            padding:30px;
                        "
                    >
                        No roles found.
                    </td>
                </tr>
            `;

      return;
    }

    tbody.innerHTML = roles.map(role => `

            <tr>

                <td>
                    ${role.id}
                </td>

                <td>
                    ${escapeHtml(role.name || '')}
                </td>

                <td>
                    ${escapeHtml(
        role.description || ''
    )}
                </td>

                <td>

                    <button
                        class="btn btn-ghost btn-sm"
                        onclick="viewRole(${role.id})"
                    >
                        View
                    </button>

                </td>

            </tr>

        `).join('');

    console.log(
        "Roles loaded successfully:",
        roles.length
    );

  } catch (error) {

    console.error(
        "ROLE LOAD ERROR:",
        error
    );

    const tbody =
        document.getElementById('rolesTableBody');

    if (tbody) {

      tbody.innerHTML = `
                <tr>
                    <td
                        colspan="4"
                        style="
                            text-align:center;
                            padding:30px;
                            color:red;
                        "
                    >
                        Could not load roles.
                    </td>
                </tr>
            `;
    }
  }
}
async function viewRole(id) {

  console.log("VIEW ROLE CLICKED:", id);

  try {

    const response =
        await getJson('/api/admin/roles/' + id);

    console.log(
        "ROLE DETAIL RESPONSE:",
        response
    );

    const role = response.data;

    if (!role) {
      throw new Error(
          "Role data not found"
      );
    }

    const permissions =
        role.permissions || [];

    const permissionHtml =
        permissions.length > 0

            ? permissions.map(permission => `
                    <div style="
                        padding:10px 14px;
                        margin-bottom:8px;
                        border:1px solid var(--border);
                        border-radius:8px;
                        background:var(--surface);
                    ">
                        ✅ ${escapeHtml(permission)}
                    </div>
                `).join('')

            : `
                    <div style="
                        padding:15px;
                        color:var(--ink-muted);
                    ">
                        No permissions assigned.
                    </div>
                `;

    document.getElementById(
        'contentArea'
    ).innerHTML = `

            <div class="card card-pad">

                <div
                    class="flex-between"
                    style="margin-bottom:20px;"
                >

                    <div>

                        <p class="eyebrow">
                            ROLE DETAILS
                        </p>

                        <h2 style="
                            font-family:var(--font-display);
                            margin:0;
                        ">
                            ${escapeHtml(role.name)}
                        </h2>

                        <p style="
                            margin-top:8px;
                            color:var(--ink-muted);
                        ">
                            ${escapeHtml(
        role.description || ''
    )}
                        </p>

                    </div>

                    <button
                        class="btn btn-ghost"
                        id="backToRolesBtn"
                    >
                        ← Back
                    </button>

                </div>

                <h3 style="
                    font-family:var(--font-display);
                    margin-bottom:14px;
                ">
                    Assigned Permissions
                </h3>

                <div>
                    ${permissionHtml}
                </div>

            </div>
        `;

    document
        .getElementById('backToRolesBtn')
        .addEventListener(
            'click',
            renderRolesPermissions
        );

  } catch (error) {

    console.error(
        "ROLE DETAIL ERROR:",
        error
    );

    showToast(
        error.message ||
        "Could not load role details",
        "error"
    );
  }
}
async function renderAdminUserManagement() {
  document.getElementById('pageActions').innerHTML =
      `<button class="btn btn-primary" id="addStaffBtn">+ Add Staff</button>`;
  document.getElementById('addStaffBtn').addEventListener('click', openStaffModal);

  document.getElementById('contentArea').innerHTML = `
    <div class="flex-between" style="margin-bottom:14px;">
      <div class="nav-list" style="flex-direction:row; gap:8px;">
        <span class="nav-item active" id="tabStaff" style="background:var(--surface); color:var(--primary); border-left:none; border-radius:8px; padding:8px 14px;">Staff</span>
        <span class="nav-item" id="tabOwners" style="background:transparent; color:var(--ink-muted); border-left:none; border-radius:8px; padding:8px 14px; cursor:pointer;">Admins & Owners</span>
        <span class="nav-item" id="tabCustomers" style="background:transparent; color:var(--ink-muted); border-left:none; border-radius:8px; padding:8px 14px; cursor:pointer;">Customers</span>
      </div>
    </div>
    <div class="table-wrap">
      <table>
        <thead>
          <tr><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th>Verified</th><th></th></tr>
        </thead>
        <tbody id="usersTableBody"></tbody>
      </table>
    </div>
  `;

  document.getElementById('tabStaff').addEventListener('click', () => loadUsers('staff'));
  document.getElementById('tabOwners').addEventListener('click', () => loadUsers('owners'));
  document.getElementById('tabCustomers').addEventListener('click', () => loadUsers('customers'));

  await loadUsers('staff');
  await refreshAdminStats();
}

async function refreshAdminStats() {
  try {
    const [staff, customers] = await Promise.all([
      getJson('/api/admin/users/staff'),
      getJson('/api/admin/users/customers')
    ]);
    const staffList = staff.data;
    const customerList = customers.data;
    const activeStaff = staffList.filter(u => u.status === 'ACTIVE').length;
    const unverified = customerList.filter(u => !u.emailVerified).length;

    const cards = document.querySelectorAll('#statGrid .stat-value');
    if (cards.length >= 4) {
      cards[0].textContent = staffList.length + customerList.length;
      cards[1].textContent = activeStaff;
      cards[2].textContent = customerList.length;
      cards[3].textContent = unverified;
    }
  } catch (e) { /* stats are a nice-to-have; ignore failures */ }
}

async function loadUsers(type) {
  activeUserTab = type;
  const tabs = { staff: 'tabStaff', owners: 'tabOwners', customers: 'tabCustomers' };
  Object.entries(tabs).forEach(([key, id]) => {
    const el = document.getElementById(id);
    el.style.background = key === type ? 'var(--surface)' : 'transparent';
    el.style.color = key === type ? 'var(--primary)' : 'var(--ink-muted)';
  });

  try {
    // "owners" has no dedicated endpoint - it's the staff list filtered to ADMIN / PHARMACY_OWNER
    const fetchType = type === 'owners' ? 'staff' : type;
    const res = await getJson('/api/admin/users/' + fetchType);
    const list = type === 'owners'
        ? res.data.filter(u => u.role === 'ADMIN' || u.role === 'PHARMACY_OWNER')
        : res.data;

    const rows = list.map(u => `
      <tr>
        <td class="cell-name">${escapeHtml(u.fullName)}</td>
        <td class="cell-mono">${escapeHtml(u.email)}</td>
        <td class="cell-mono">${escapeHtml(u.phoneNumber)}</td>
        <td>${roleBadge(u.role)}</td>
        <td>${statusPill(u.status)}</td>
        <td>${u.emailVerified ? '✅ email' : '❌ email'} · ${u.phoneVerified ? '✅ phone' : '❌ phone'}</td>
        <td class="cell-actions">
          <button class="btn btn-ghost btn-sm" data-action="edit" data-id="${u.id}">Edit</button>
          ${u.status === 'ACTIVE'
        ? `<button class="btn btn-ghost btn-sm" data-action="suspend" data-id="${u.id}">Suspend</button>`
        : `<button class="btn btn-ghost btn-sm" data-action="activate" data-id="${u.id}">Activate</button>`}
          <button class="btn btn-danger btn-sm" data-action="delete" data-id="${u.id}">Delete</button>
        </td>
      </tr>
    `).join('');
    document.getElementById('usersTableBody').innerHTML = rows || `<tr><td colspan="7" class="text-muted" style="text-align:center; padding:30px;">No records yet.</td></tr>`;

    lastLoadedUsers = {};
    list.forEach(u => { lastLoadedUsers[u.id] = u; });

    document.querySelectorAll('[data-action]').forEach(btn => {
      btn.addEventListener('click', () => onUserAction(btn.dataset.action, btn.dataset.id, type));
    });
  } catch (e) {
    showToast(e.message || 'Could not load users', 'error');
  }
}

async function onUserAction(action, id, type) {
  if (action === 'edit') {
    openEditUserModal(lastLoadedUsers[id]);
    return;
  }
  try {
    if (action === 'suspend') {
      await patchStatus(id, 'SUSPENDED');
    } else if (action === 'activate') {
      await patchStatus(id, 'ACTIVE');
    } else if (action === 'delete') {
      if (!confirm('Delete this user? This cannot be undone.')) return;
      await deleteUser(id);
    }
    showToast('Updated successfully', 'success');
    await loadUsers(type);
    await refreshAdminStats();
  } catch (e) {
    showToast(e.message || 'Action failed', 'error');
  }
}

async function patchStatus(id, status) {
  const res = await fetch(`/api/admin/users/${id}/status?status=${status}`, { method: 'PATCH', credentials: 'same-origin' });
  const data = await res.json();
  if (!res.ok || data.success === false) throw new Error(data.message);
  return data;
}

async function deleteUser(id) {
  const res = await fetch(`/api/admin/users/${id}`, { method: 'DELETE', credentials: 'same-origin' });
  const data = await res.json();
  if (!res.ok || data.success === false) throw new Error(data.message);
  return data;
}

function roleBadge(role) {
  const cls = role === 'ADMIN' ? 'badge-role-admin'
      : role === 'PHARMACY_OWNER' ? 'badge-role-manager'
          : role === 'CUSTOMER' ? 'badge-role-default'
              : (role === 'PHARMACIST' || role === 'INVENTORY_MANAGER') ? 'badge-role-pharmacist'
                  : role === 'CASHIER' ? 'badge-role-cashier'
                      : 'badge-role-manager';
  return `<span class="badge ${cls}">${role.replace('_', ' ')}</span>`;
}

function statusPill(status) {
  const cls = status === 'ACTIVE' ? 'status-active' : status === 'SUSPENDED' ? 'status-suspended' : 'status-inactive';
  return `<span class="status-pill ${cls}"><span class="status-dot"></span>${status}</span>`;
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str || '';
  return div.innerHTML;
}

// ---------------- Add staff modal ----------------

function openStaffModal() {
  document.getElementById('staffModalBackdrop').classList.add('open');
}
function closeStaffModal() {
  document.getElementById('staffModalBackdrop').classList.remove('open');
  document.getElementById('staffForm').reset();
  clearErrors(['s_fullNameField', 's_emailField', 's_phoneNumberField', 's_roleField', 's_passwordField']);
}

// ---------------- Edit user modal (admin / pharmacy owner) ----------------

function openEditUserModal(user) {
  if (!user) return;
  document.getElementById('e_id').value = user.id;
  document.getElementById('e_fullName').value = user.fullName;
  document.getElementById('e_email').value = user.email;
  document.getElementById('e_phoneNumber').value = user.phoneNumber;
  document.getElementById('e_role').value = user.role;
  document.getElementById('e_status').value = user.status;
  document.getElementById('editUserModalBackdrop').classList.add('open');
}
function closeEditUserModal() {
  document.getElementById('editUserModalBackdrop').classList.remove('open');
  clearErrors(['e_fullNameField', 'e_emailField', 'e_phoneNumberField', 'e_roleField', 'e_statusField']);
}

const EDIT_USER_FIELD_IDS = ['e_fullNameField', 'e_emailField', 'e_phoneNumberField', 'e_roleField', 'e_statusField'];

async function submitEditUser() {
  clearErrors(EDIT_USER_FIELD_IDS);
  const id = document.getElementById('e_id').value;
  try {
    const res = await putJson(`/api/admin/users/${id}`, {
      fullName: document.getElementById('e_fullName').value.trim(),
      email: document.getElementById('e_email').value.trim(),
      phoneNumber: document.getElementById('e_phoneNumber').value.trim(),
      role: document.getElementById('e_role').value,
      status: document.getElementById('e_status').value
    });
    showToast(res.message, 'success');
    closeEditUserModal();
    await loadUsers(activeUserTab);
    await refreshAdminStats();
  } catch (err) {
    handleErrorResponse(err, EDIT_USER_FIELD_IDS);
  }
}

// ---------------- Customer: My Account ----------------

async function renderMyAccount() {
  document.getElementById('pageActions').innerHTML = '';
  document.getElementById('contentArea').innerHTML = `
    <div class="card card-pad" style="max-width:560px; margin-bottom:20px;">
      <h2 style="font-family:var(--font-display); font-size:18px; margin:0 0 16px;">Profile details</h2>
      <form id="accountForm">
        <div class="field" id="a_fullNameField">
          <label>Full name</label>
          <input type="text" id="a_fullName" required>
          <div class="error"></div>
        </div>
        <div class="field">
          <label>Email</label>
          <input type="email" id="a_email" disabled>
          <div class="hint">Email can't be changed here - contact support if it's wrong.</div>
        </div>
        <div class="field" id="a_phoneNumberField">
          <label>Phone number</label>
          <input type="text" id="a_phoneNumber" placeholder="07XXXXXXXX" required>
          <div class="error"></div>
        </div>
        <button type="submit" class="btn btn-primary">Save changes</button>
      </form>
    </div>

    <div class="card card-pad" style="max-width:560px;">
      <h2 style="font-family:var(--font-display); font-size:18px; margin:0 0 16px;">Change password</h2>
      <form id="passwordForm">
        <div class="field" id="p_currentPasswordField">
          <label>Current password</label>
          <input type="password" id="p_currentPassword" required>
          <div class="error"></div>
        </div>
        <div class="field" id="p_newPasswordField">
          <label>New password</label>
          <input type="password" id="p_newPassword" required>
          <div class="hint">Min 8 characters, upper &amp; lower case, a number and a symbol</div>
          <div class="error"></div>
        </div>
        <button type="submit" class="btn btn-primary">Update password</button>
      </form>
    </div>
  `;

  try {
    const res = await getJson('/api/me');
    document.getElementById('a_fullName').value = res.data.fullName;
    document.getElementById('a_email').value = res.data.email;
    document.getElementById('a_phoneNumber').value = res.data.phoneNumber;
  } catch (e) {
    showToast(e.message || 'Could not load your profile', 'error');
  }

  document.getElementById('accountForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearErrors(['a_fullNameField', 'a_phoneNumberField']);
    try {
      const res = await putJson('/api/me', {
        fullName: document.getElementById('a_fullName').value.trim(),
        phoneNumber: document.getElementById('a_phoneNumber').value.trim()
      });
      showToast(res.message, 'success');
      document.getElementById('whoName').textContent = res.data.fullName;
    } catch (err) {
      handleErrorResponse(err, ['a_fullNameField', 'a_phoneNumberField']);
    }
  });

  document.getElementById('passwordForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearErrors(['p_currentPasswordField', 'p_newPasswordField']);
    try {
      const res = await postJson('/api/me/change-password', {
        currentPassword: document.getElementById('p_currentPassword').value,
        newPassword: document.getElementById('p_newPassword').value
      });
      showToast(res.message, 'success');
      document.getElementById('passwordForm').reset();
    } catch (err) {
      handleErrorResponse(err, ['p_currentPasswordField', 'p_newPasswordField']);
    }
  });
}

document.addEventListener('DOMContentLoaded', () => {
  init();
  document.getElementById('closeStaffModal').addEventListener('click', closeStaffModal);
  document.getElementById('cancelStaffModal').addEventListener('click', closeStaffModal);
  document.getElementById('submitStaffModal').addEventListener('click', async () => {
    clearErrors(['s_fullNameField', 's_emailField', 's_phoneNumberField', 's_roleField', 's_passwordField']);
    try {
      const res = await postJson('/api/admin/users/staff', {
        fullName: document.getElementById('s_fullName').value.trim(),
        email: document.getElementById('s_email').value.trim(),
        phoneNumber: document.getElementById('s_phoneNumber').value.trim(),
        role: document.getElementById('s_role').value,
        password: document.getElementById('s_password').value
      });
      showToast(res.message, 'success');
      closeStaffModal();
      await loadUsers('staff');
      await refreshAdminStats();
    } catch (err) {
      handleErrorResponse(err, ['s_fullNameField', 's_emailField', 's_phoneNumberField', 's_roleField', 's_passwordField']);
    }
  });

  document.getElementById('closeEditUserModal').addEventListener('click', closeEditUserModal);
  document.getElementById('cancelEditUserModal').addEventListener('click', closeEditUserModal);
  document.getElementById('submitEditUserModal').addEventListener('click', submitEditUser);
});

async function logout() {
  const endpoint = currentRole === 'CUSTOMER' ? '/customer/logout' : '/manager/logout';
  try {
    await postJson(endpoint, {});
  } catch (e) { /* ignore */ }
  window.location.href = currentRole === 'CUSTOMER' ? '/customer/login.html' : '/manager/login.html';
}