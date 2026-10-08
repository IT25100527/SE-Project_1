// Shared helpers used by every page: fetch wrapper, toast, inline field errors.

async function postJson(url, body) {
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'same-origin',
    body: JSON.stringify(body)
  });
  const data = await res.json();
  if (!res.ok || data.success === false) {
    const err = new Error(data.message || 'Request failed');
    err.fieldErrors = (typeof data.data === 'object' && data.data) ? data.data : null;
    throw err;
  }
  return data;
}

async function getJson(url) {
  const res = await fetch(url, { credentials: 'same-origin' });
  const data = await res.json();
  if (!res.ok || data.success === false) {
    const err = new Error(data.message || 'Request failed');
    throw err;
  }
  return data;
}

async function putJson(url, body) {
  const res = await fetch(url, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'same-origin',
    body: JSON.stringify(body)
  });
  const data = await res.json();
  if (!res.ok || data.success === false) {
    const err = new Error(data.message || 'Request failed');
    err.fieldErrors = (typeof data.data === 'object' && data.data) ? data.data : null;
    throw err;
  }
  return data;
}

function showToast(message, type) {
  const stack = document.getElementById('toastStack');
  if (!stack) return;
  const toast = document.createElement('div');
  toast.className = 'toast' + (type ? ' ' + type : '');
  toast.textContent = message;
  stack.appendChild(toast);
  setTimeout(() => toast.remove(), 4000);
}

function clearErrors(fieldIds) {
  fieldIds.forEach(id => {
    const field = document.getElementById(id);
    if (!field) return;
    field.classList.remove('has-error');
    const err = field.querySelector('.error');
    if (err) err.textContent = '';
  });
}

// Maps a backend field name (fullName, phoneNumber, ...) to the "<name>Field" wrapper div,
// falls back to a toast when a field can't be matched (e.g. a top-level message).
function handleErrorResponse(err, fieldIds) {
  if (err.fieldErrors) {
    Object.entries(err.fieldErrors).forEach(([field, message]) => {
      const wrapperId = field + 'Field';
      const wrapper = document.getElementById(wrapperId);
      if (wrapper && fieldIds.includes(wrapperId)) {
        wrapper.classList.add('has-error');
        const err2 = wrapper.querySelector('.error');
        if (err2) err2.textContent = message;
      }
    });
    showToast(err.message || 'Please fix the errors below', 'error');
  } else {
    showToast(err.message || 'Something went wrong', 'error');
  }
}
