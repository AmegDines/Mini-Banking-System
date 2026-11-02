const API = '/api';
const $ = (id) => document.getElementById(id);
const accountsContainer = $('accountsContainer');
const notificationArea = $('notificationArea');

document.addEventListener('DOMContentLoaded', fetchAccounts);

$('createAccountForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  await request('/accounts', {
    method: 'POST', body: JSON.stringify({
      name: $('ownerName').value.trim(), email: $('ownerEmail').value.trim(),
      accountType: $('accountType').value, initialBalance: Number($('initialBalance').value)
    })
  }, 'Account created successfully!');
  $('createAccountForm').reset();
  fetchAccounts();
});

$('depositBtn').addEventListener('click', () => submitAmount('deposit'));
$('withdrawBtn').addEventListener('click', () => submitAmount('withdraw'));
async function submitAmount(type) {
  const accountNumber = $('dwAccountId').value;
  const amount = Number($('dwAmount').value);
  if (!accountNumber || !amount) return showNotification('Select an account and enter a positive amount.', 'error');
  const ok = await request(`/transactions/${type}`, { method: 'POST', body: JSON.stringify({ accountNumber, amount }) }, `${type === 'deposit' ? 'Deposit' : 'Withdrawal'} completed.`);
  if (ok) { $('dwAmount').value = ''; fetchAccounts(); }
}

$('transferForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const senderAccNo = $('fromAccountId').value;
  const receiverAccNo = $('toAccountId').value;
  const amount = Number($('transferAmount').value);
  if (senderAccNo === receiverAccNo) return showNotification('Choose two different accounts.', 'error');
  const ok = await request('/transactions/transfer', { method: 'POST', body: JSON.stringify({ senderAccNo, receiverAccNo, amount }) }, 'Transfer completed.');
  if (ok) { $('transferForm').reset(); fetchAccounts(); }
});

async function request(path, options, successMessage) {
  try {
    const response = await fetch(API + path, { ...options, headers: { 'Content-Type': 'application/json', ...(options.headers || {}) } });
    const payload = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(payload.message || 'Request failed.');
    showNotification(successMessage, 'success');
    return true;
  } catch (error) { showNotification(error.message || 'Request failed.', 'error'); return false; }
}

async function fetchAccounts() {
  try {
    const response = await fetch(`${API}/accounts`);
    if (!response.ok) throw new Error('Unable to load accounts.');
    const accounts = await response.json();
    accountsContainer.replaceChildren();
    if (!accounts.length) {
      const empty = document.createElement('p'); empty.textContent = 'No accounts found. Create one to get started!'; accountsContainer.append(empty);
    }
    accounts.forEach((account) => {
      const card = document.createElement('article'); card.className = 'account-card';
      const heading = document.createElement('div'); heading.className = 'acc-header';
      const number = document.createElement('span'); number.className = 'acc-id'; number.textContent = account.accountNumber;
      const name = document.createElement('span'); name.className = 'acc-name'; name.textContent = account.ownerName;
      heading.append(number, name);
      const type = document.createElement('p'); type.textContent = account.accountType;
      const balance = document.createElement('div'); balance.className = 'acc-balance'; balance.textContent = `$${Number(account.balance).toFixed(2)}`;
      card.append(heading, type, balance); accountsContainer.append(card);
    });
    updateSelects(accounts);
  } catch (error) { showNotification(error.message, 'error'); }
}

function updateSelects(accounts) {
  [['dwAccountId', 'Select Account'], ['fromAccountId', 'From Account'], ['toAccountId', 'To Account']].forEach(([id, label]) => {
    const select = $(id); select.replaceChildren(new Option(label, '', true, true));
    select.options[0].disabled = true;
    accounts.forEach((account) => select.add(new Option(`${account.ownerName} (${account.accountNumber})`, account.accountNumber)));
  });
}

function showNotification(message, type) {
  const toast = document.createElement('div'); toast.className = `toast ${type}`;
  const text = document.createElement('span'); text.textContent = message;
  const close = document.createElement('button'); close.type = 'button'; close.textContent = '×'; close.setAttribute('aria-label', 'Dismiss'); close.onclick = () => toast.remove();
  toast.append(text, close); notificationArea.prepend(toast);
  setTimeout(() => toast.remove(), 5000);
}
