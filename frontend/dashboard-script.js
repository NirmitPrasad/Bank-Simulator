// Dashboard functionality
document.addEventListener('DOMContentLoaded', function() {
    // Initialize dashboard
    initializeDashboard();
    
    // Set up form handlers
    setupFormHandlers();
    
    // Load initial data
    loadInitialData();
});

// Initialize dashboard
function initializeDashboard() {
    console.log('Dashboard initialized');
    
    // Set up navigation click handlers
    setupNavigationHandlers();
    
    // Show dashboard section by default
    showSection('dashboard');
}

// Setup navigation click handlers
function setupNavigationHandlers() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const sectionName = this.getAttribute('onclick').match(/showSection\('([^']+)'\)/)[1];
            console.log('Navigation clicked:', sectionName);
            showSection(sectionName);
        });
    });
}

// Navigation functionality
function showSection(sectionName) {
    console.log('Showing section:', sectionName);
    
    // Hide all sections
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        section.classList.remove('active');
        section.style.display = 'none';
    });
    
    // Remove active class from all nav items
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.classList.remove('active');
    });
    
    // Show selected section
    const targetSection = document.getElementById(sectionName + '-section');
    if (targetSection) {
        targetSection.classList.add('active');
        targetSection.style.display = 'block';
        targetSection.style.opacity = '1';
        targetSection.style.visibility = 'visible';
        console.log('Section displayed:', targetSection.id);
    } else {
        console.error('Section not found:', sectionName + '-section');
    }
    
    // Add active class to the corresponding nav item
    const correspondingNavItem = document.querySelector(`[onclick*="${sectionName}"]`);
    if (correspondingNavItem) {
        correspondingNavItem.classList.add('active');
    }
    
    // Load section-specific data
    loadSectionData(sectionName);
}

// Load section-specific data
function loadSectionData(sectionName) {
    switch(sectionName) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'accounts':
            loadAccounts();
            break;
        case 'create-account':
            // Form is already loaded
            break;
        case 'transaction':
            // Form is already loaded
            break;
        case 'transfer':
            // Form is already loaded
            break;
        case 'history':
            loadTransactionHistory();
            break;
    }
}

// Load dashboard data
function loadDashboardData() {
    // Fetch accounts and update dashboard cards
    fetch('http://localhost:8080/api/accounts/all')
      .then(res => res.json())
      .then(accounts => {
        // Handle empty response or null accounts
        if (!accounts) accounts = [];
        const totalBalance = (Array.isArray(accounts) ? accounts : []).reduce((sum, a) => sum + (parseFloat(a.balance || 0) || 0), 0);
        const hasAccount = Array.isArray(accounts) && accounts.length > 0;

        // Set notifications count
        const notificationsElement = document.querySelector('.notifications-value');
        if (notificationsElement) notificationsElement.textContent = hasAccount ? '1' : '0';

        // Populate recent notifications box
        const recentInfo = document.querySelector('.recent-notifications-card .card-info');
        if (recentInfo) {
            if (hasAccount) {
                recentInfo.innerHTML = '<h3>Recent Notifications</h3>' +
                  '<div class="notifications-list">' +
                  '  <div class="notification-item">' +
                  '    <div class="notification-icon">✅</div>' +
                  '    <div class="notification-content">' +
                  '      <div class="notification-title">Account created successfully</div>' +
                  '      <div class="notification-subtext">Just now</div>' +
                  '    </div>' +
                  '    <div class="notification-badge">SUCCESS</div>' +
                  '  </div>' +
                  '</div>';
            } else {
                recentInfo.innerHTML = '<h3>Recent Notifications</h3><div class="empty-notifications">No notifications</div>';
            }
        }

        updateDashboardCards({
          totalBalance,
          totalAccounts: Array.isArray(accounts) ? accounts.length : 0,
          notifications: hasAccount ? 1 : 0
        });
      })
      .catch(() => updateDashboardCards({ totalBalance: 0, totalAccounts: 0, notifications: 0 }));
}

// Update dashboard cards with data
function updateDashboardCards(data) {
    const balanceElement = document.querySelector('.balance-value');
    const accountsElement = document.querySelector('.accounts-value');
    const notificationsElement = document.querySelector('.notifications-value');
    
    if (balanceElement) {
        balanceElement.textContent = `₹${data.totalBalance.toFixed(2)}`;
    }
    
    if (accountsElement) {
        accountsElement.textContent = data.totalAccounts;
    }
    
    if (notificationsElement) {
        notificationsElement.textContent = data.notifications;
    }
}

// Load accounts data
function loadAccounts() {
    console.log('Loading accounts...');
    const container = document.querySelector('.accounts-container');
    if (!container) return;

    fetch('http://localhost:8080/api/accounts/all')
        .then(res => res.json())
        .then(accounts => {
            // Handle empty response or null accounts
            if (!accounts || !Array.isArray(accounts) || accounts.length === 0) {
                container.innerHTML = '<div class="empty-state"><div class="empty-icon">💳</div><p>No accounts found. Create one to get started!</p></div>';
                updateDashboardCards({ totalBalance: 0, totalAccounts: 0, notifications: 0 });
                return;
            }

            // Render full-width account card layout
            let totalBalance = 0;
            const rows = accounts.map(acc => {
                const balance = parseFloat(acc.balance || 0);
                totalBalance += isNaN(balance) ? 0 : balance;
                const typeLabel = acc.accountType ? acc.accountType.toString().toUpperCase() : 'ACCOUNT';
                return `
                <div class="account-card">
                  <div class="account-icon-large">💳</div>
                  <div class="account-main">
                    <div class="account-title-row">
                      <span class="account-type-chip">${typeLabel}</span>
                      <span class="account-name">${acc.accountHolderName || acc.nameOnAccount || ''}</span>
                    </div>
                    <div class="account-meta">
                      <div><span class="label">Account No:</span> ${acc.accountNumber || ''}</div>
                      <div><span class="label">Bank:</span> ${acc.bankName || ''} • ${acc.branch || ''}</div>
                      <div><span class="label">IFSC:</span> ${acc.ifscCode || ''}</div>
                      <div><span class="label">Phone:</span> ${acc.phoneLinked || ''}</div>
                      <div><span class="label">Email:</span> ${acc.email || ''}</div>
                    </div>
                  </div>
                  <div class="account-balance">
                    <div class="amount">₹${(balance).toFixed(2)}</div>
                    <div class="sub">Current Balance</div>
                  </div>
                </div>`;
            }).join('');

            container.innerHTML = `<div class="accounts-grid">${rows}</div>`;
            updateDashboardCards({ totalBalance, totalAccounts: accounts.length, notifications: 0 });
        })
        .catch(err => {
            console.error('Failed to load accounts', err);
            container.innerHTML = '<div class="empty-state"><div class="empty-icon">❗</div><p>Failed to load accounts.</p></div>';
        });
}

// Load transaction history
function loadTransactionHistory() {
    console.log('Loading transaction history...');
    const container = document.querySelector('#history-section .history-container');
    if (!container) return;
    
    // Load all transactions across all accounts
    fetch('http://localhost:8080/api/accounts/transactions')
      .then(res => res.json())
      .then(list => renderTransactionHistory(list))
      .catch(() => {
        container.innerHTML = '<div class="empty-state"><div class="empty-icon">📊</div><p>No transactions found. Start by creating an account!</p></div>';
      });
}

// Keep support to fetch a specific account's transactions if needed
function fetchTransactionHistory(accountNumber) {
    const container = document.querySelector('#history-section .history-container');
    if (!container) return;
    if (!accountNumber) {
        return loadTransactionHistory();
        return;
    }
    
    container.innerHTML = '<div class="empty-state"><div class="empty-icon">⏳</div><p>Loading transactions...</p></div>';
    fetch(`http://localhost:8080/api/accounts/${encodeURIComponent(accountNumber)}/transactions`)
      .then(res => res.json())
      .then(list => {
        renderTransactionHistory(list);
      })
      .catch(() => {
        container.innerHTML = '<div class="empty-state"><div class="empty-icon">❗</div><p>Failed to load transactions.</p></div>';
      });
}

function renderTransactionHistory(list) {
    const container = document.querySelector('#history-section .history-container');
    if (!container) return;
    try {
        if (!Array.isArray(list) || list.length === 0) {
            container.innerHTML = '<div class="empty-state"><div class="empty-icon">📊</div><p>No transactions yet for this account.</p></div>';
            return;
        }
        const rows = list.map(tx => {
            const typeRaw = (tx.transactionType || tx.transaction_type || '').toString();
            const type = typeRaw.toUpperCase();
            const isDeposit = type.includes('DEPOSIT') || type.includes('CREDIT') || type === 'TRANSFER_CREDIT';
            const sign = isDeposit ? '+' : '-';
            const amt = Number(tx.transactionAmount ?? tx.transaction_amount ?? 0).toFixed(2);
            const dtSrc = tx.transactionDate || tx.transaction_date;
            const dt = dtSrc ? new Date(dtSrc).toLocaleString() : '';
            const badgeClass = isDeposit ? 'deposit' : 'withdrawal';
            const typeLabel = isDeposit ? 'Credited' : 'Debited';
            const utr = (tx.utrNumber || tx.utr_number || '') + '';
            const accNo = tx.accountNumber || tx.account_number || '';
            return `
              <tr>
                <td>${dt}</td>
                <td>${accNo}</td>
                <td><span class="tx-badge ${badgeClass}">${typeLabel}</span></td>
                <td style="text-align:right; font-weight:700; color:${sign==='+' ? '#27ae60' : '#e74c3c'}">${sign}₹${amt}</td>
                <td style="font-family: monospace; color:#6b7a90">${utr}</td>
              </tr>`;
        }).join('');
        container.innerHTML = `
          <div style="width:100%">
            <table class="history-table">
              <thead>
                <tr>
                  <th style="text-align:left; color:#6b7a90;">Date & Time</th>
                  <th style="text-align:left; color:#6b7a90;">Account No.</th>
                  <th style="text-align:left; color:#6b7a90;">Type</th>
                  <th style="text-align:right; color:#6b7a90;">Amount</th>
                  <th style="text-align:left; color:#6b7a90;">UTR</th>
                </tr>
              </thead>
              <tbody>${rows}</tbody>
            </table>
          </div>`;
    } catch (e) {
        container.innerHTML = '<div class="empty-state"><div class="empty-icon">❗</div><p>Failed to load transactions.</p></div>';
    }
}

// Transaction success modal controls
function openTxSuccessModal() {
    const modal = document.getElementById('txSuccessModal');
    if (modal) {
        modal.classList.add('show');
        modal.style.display = 'flex';
    }
}

function closeTxSuccessModal() {
    const modal = document.getElementById('txSuccessModal');
    if (modal) {
        modal.classList.remove('show');
        modal.style.display = 'none';
    }
}

// Setup form handlers
function setupFormHandlers() {
    // Add Account Form
    const createAccountForm = document.getElementById('createAccountForm');
    if (createAccountForm) {
        createAccountForm.addEventListener('submit', handleCreateAccount);
    }
    
    // Transaction Form
    const transactionForm = document.getElementById('transactionForm');
    if (transactionForm) {
        transactionForm.addEventListener('submit', handleTransaction);
    }
    
    // Transfer Form
    const transferForm = document.getElementById('transferForm');
    if (transferForm) {
        transferForm.addEventListener('submit', handleTransfer);
    }
}

// Handle add account form submission
function handleCreateAccount(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const accountData = {
        accountType: formData.get('accountType'),
        balance: parseFloat(formData.get('initialDeposit') || '0'),
        bankName: 'AXIS Bank',
        branch: 'Main Branch',
        nameOnAccount: 'User',
        accountHolderName: 'User',
        email: 'user@example.com',
        phoneLinked: '9876333399',
        ifscCode: 'TEST0000001',
        accountNumber: 'ACC-' + Date.now().toString().slice(-6) + '-' + Math.floor(Math.random()*1000).toString().padStart(3,'0')
    };

    console.log('Creating account:', accountData);

    const submitBtn = e.target.querySelector('.btn-primary');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Creating...';
    submitBtn.disabled = true;

    fetch('http://localhost:8080/api/accounts/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(accountData)
    })
    .then(async res => {
        if (!res.ok) throw new Error(await res.text());
        return res.json();
    })
    .then(() => {
        e.target.reset();
        showSection('accounts');
        loadAccounts();
    })
    .catch(err => {
        console.error('Failed to create account:', err);
        showNotification('Failed to create account: ' + (err.message || 'Unknown error'), 'error', 'Account Creation Failed');
    })
    .finally(() => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    });
}

// Handle transaction form submission
function handleTransaction(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const rawType = (formData.get('transactionType') || '').toString().toUpperCase();
    const normalizedType = rawType === 'WITHDRAWAL' ? 'WITHDRAW' : 'DEPOSIT';
    const transactionData = {
        type: normalizedType,
        accountNumber: formData.get('accountNumber'),
        amount: parseFloat(formData.get('amount')),
        mpin: formData.get('mpin')
    };
    
    console.log('Processing transaction:', transactionData);
    
    // Show loading state
    const submitBtn = e.target.querySelector('.btn-primary');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Processing...';
    submitBtn.disabled = true;
    
    fetch('http://localhost:8080/api/accounts/transactions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(transactionData)
    })
    .then(async res => {
        const dataText = await res.text();
        if (!res.ok) throw new Error(dataText || 'Transaction failed');
        try { return JSON.parse(dataText); } catch { return { message: 'Success' }; }
    })
    .then((payload) => {
        // Refresh accounts and dashboard
        loadAccounts();
        loadDashboardData();
        // Refresh history for this account
        if (transactionData.accountNumber) {
            fetchTransactionHistory(transactionData.accountNumber);
        } else {
            loadTransactionHistory();
        }
        // Show success modal
        const msgEl = document.getElementById('txSuccessMessage');
        if (msgEl) {
            const amountFmt = Number(transactionData.amount).toFixed(2);
            const typeLabel = normalizedType === 'DEPOSIT' ? 'Credited' : 'Debited';
            const utr = payload && payload.utr ? ` (UTR: ${payload.utr})` : '';
            msgEl.innerHTML = `<div style="display:flex; align-items:center; gap:12px;">
                <div class="alert-icon">💸</div>
                <div>
                    <div style="font-weight:800; color:#fff; font-size:18px;">Transaction Successful</div>
                    <div style="color:#eef;">
                        <b>${typeLabel}</b> of <b>₹${amountFmt}</b> for account <b>${transactionData.accountNumber}</b> completed successfully${utr}.
                    </div>
                </div>
            </div>`;
        }
        openTxSuccessModal();
        // Reset form and button
        e.target.reset();
    })
    .catch(err => {
        console.error('Transaction failed:', err);
        const errorMessage = err.message || 'Unknown error';
        if (errorMessage.includes('MPIN')) {
            showNotification(errorMessage, 'error', 'MPIN Error');
        } else {
            showNotification('Transaction failed: ' + errorMessage, 'error', 'Transaction Failed');
        }
    })
    .finally(() => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    });
}

// Handle transfer form submission
function handleTransfer(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const transferData = {
        fromAccountNumber: formData.get('fromAccount'),
        toAccountNumber: formData.get('toAccount'),
        amount: parseFloat(formData.get('transferAmount')),
        description: 'Account to account transfer',
        mpin: formData.get('mpin')
    };
    
    console.log('Processing transfer:', transferData);
    
    // Show loading state
    const submitBtn = e.target.querySelector('.btn-primary');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Transferring...';
    submitBtn.disabled = true;
    
    fetch('http://localhost:8080/api/accounts/transfer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(transferData)
    })
    .then(async res => {
        const dataText = await res.text();
        if (!res.ok) throw new Error(dataText || 'Transfer failed');
        try { return JSON.parse(dataText); } catch { return { message: 'Success' }; }
    })
    .then((payload) => {
        // Refresh both accounts data and history
        loadAccounts();
        loadDashboardData();
        loadTransactionHistory(); // all accounts

        // Success modal content
        const msgEl = document.getElementById('txSuccessMessage');
        if (msgEl) {
            const amountFmt = Number(transferData.amount).toFixed(2);
            const fromAcc = transferData.fromAccountNumber;
            const toAcc = transferData.toAccountNumber;
            const utr = payload && payload.utr ? ` (UTR: ${payload.utr})` : '';
            msgEl.innerHTML = `<div style="display:flex; align-items:center; gap:12px;">
                <div class="alert-icon">💸</div>
                <div>
                    <div style="font-weight:800; color:#fff; font-size:18px;">Transfer Successful</div>
                    <div style="color:#eef;">
                        Sent <b>₹${amountFmt}</b> from <b>${fromAcc}</b> to <b>${toAcc}</b>${utr}.
                    </div>
                </div>
            </div>`;
        }
        openTxSuccessModal();

        e.target.reset();
    })
    .catch(err => {
        console.error('Transfer failed:', err);
        const errorMessage = err.message || 'Unknown error';
        if (errorMessage.includes('MPIN')) {
            showNotification(errorMessage, 'error', 'MPIN Error');
        } else {
            showNotification('Transfer failed: ' + errorMessage, 'error', 'Transfer Failed');
        }
    })
    .finally(() => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    });
}

// Refresh accounts
function refreshAccounts() {
    console.log('Refreshing accounts...');
    
    const refreshBtn = document.querySelector('#accounts-section .refresh-btn');
    const originalText = refreshBtn.innerHTML;
    refreshBtn.innerHTML = '<span class="refresh-icon">🔄</span> Refreshing...';
    refreshBtn.disabled = true;
    
    // Simulate API call
    setTimeout(() => {
        // Accounts refreshed
        refreshBtn.innerHTML = originalText;
        refreshBtn.disabled = false;
    }, 1500);
}

// Refresh transaction history
function refreshHistory() {
    console.log('Refreshing transaction history...');
    
    const refreshBtn = document.querySelector('#history-section .refresh-btn');
    const originalText = refreshBtn.innerHTML;
    refreshBtn.innerHTML = '<span class="refresh-icon">🔄</span> Refreshing...';
    refreshBtn.disabled = true;
    
    // Simulate API call
    setTimeout(() => {
        // Transaction history refreshed
        refreshBtn.innerHTML = originalText;
        refreshBtn.disabled = false;
    }, 1500);
}

// Logout functionality
function logout() {
    console.log('Showing logout modal...');
    showLogoutModal();
}

// Show custom logout modal
function showLogoutModal() {
    const modal = document.getElementById('logoutModal');
    if (modal) {
        modal.classList.add('show');
        modal.style.display = 'flex';
    }
}

// Close logout modal
function closeLogoutModal() {
    const modal = document.getElementById('logoutModal');
    if (modal) {
        modal.classList.remove('show');
        modal.style.display = 'none';
    }
}

// Confirm logout
function confirmLogout() {
    console.log('Logging out...');
    
    // Close the modal
    closeLogoutModal();
    
    // Show loading state
    const confirmBtn = document.querySelector('.logout-btn-confirm');
    const originalText = confirmBtn.textContent;
    confirmBtn.textContent = 'Logging out...';
    confirmBtn.disabled = true;
    
    // Simulate logout process
    setTimeout(() => {
        // In real implementation, this would clear session/tokens
        // and redirect to login page
        console.log('Logged out successfully!');
        
        // Redirect to login page
        window.location.href = 'index.html';
    }, 1000);
}

// Load initial data
function loadInitialData() {
    console.log('Loading initial dashboard data...');
    
    // Simulate loading user data
    setTimeout(() => {
        console.log('Dashboard data loaded');
    }, 1000);
}

// Add some interactive animations
document.addEventListener('DOMContentLoaded', function() {
    // Animate navigation items on load
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach((item, index) => {
        item.style.opacity = '0';
        item.style.transform = 'translateX(-20px)';
        item.style.transition = 'all 0.3s ease';
        
        setTimeout(() => {
            item.style.opacity = '1';
            item.style.transform = 'translateX(0)';
        }, index * 100);
    });
});

// Go to add account page
function goToAddAccount() {
    console.log('Redirecting to add account page...');
    window.location.href = 'create-account.html?from=dashboard';
}

// Increment transfer amount by ₹100
function incrementTransferAmount() {
    const transferInput = document.getElementById('transferAmount');
    if (transferInput) {
        let currentValue = parseFloat(transferInput.value) || 0;
        let newValue = currentValue + 100;
        
        // Ensure it doesn't go below 0
        if (newValue < 0) {
            newValue = 0;
        }
        
        transferInput.value = newValue;
        
        console.log('Transfer amount incremented to:', newValue);
    }
}

// Decrement transfer amount by ₹100
function decrementTransferAmount() {
    const transferInput = document.getElementById('transferAmount');
    if (transferInput) {
        let currentValue = parseFloat(transferInput.value) || 0;
        let newValue = currentValue - 100;
        
        // Ensure it doesn't go below 0
        if (newValue < 0) {
            newValue = 0;
        }
        
        transferInput.value = newValue;
        
        console.log('Transfer amount decremented to:', newValue);
    }
}

// Increment transaction amount by ₹100
function incrementTransactionAmount() {
    const transactionInput = document.getElementById('amount');
    if (transactionInput) {
        let currentValue = parseFloat(transactionInput.value) || 0;
        let newValue = currentValue + 100;
        
        // Ensure it doesn't go below 0
        if (newValue < 0) {
            newValue = 0;
        }
        
        transactionInput.value = newValue;
        
        console.log('Transaction amount incremented to:', newValue);
    }
}

// Decrement transaction amount by ₹100
function decrementTransactionAmount() {
    const transactionInput = document.getElementById('amount');
    if (transactionInput) {
        let currentValue = parseFloat(transactionInput.value) || 0;
        let newValue = currentValue - 100;
        
        // Ensure it doesn't go below 0
        if (newValue < 0) {
            newValue = 0;
        }
        
        transactionInput.value = newValue;
        
        console.log('Transaction amount decremented to:', newValue);
    }
}

// Add keyboard navigation
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        // Close logout modal if open
        closeLogoutModal();
    }
});

// Close modal when clicking outside
document.addEventListener('click', function(e) {
    const modal = document.getElementById('logoutModal');
    if (e.target === modal) {
        closeLogoutModal();
    }
});

// Add form validation
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], select[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.style.borderColor = '#e74c3c';
            isValid = false;
        } else {
            input.style.borderColor = '#e0e0e0';
        }
    });
    
    return isValid;
}

// Enhanced form submission with validation
function setupFormHandlers() {
    // Add Account Form
    const createAccountForm = document.getElementById('createAccountForm');
    if (createAccountForm) {
        createAccountForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (validateForm(this)) {
                handleCreateAccount(e);
            } else {
                // Please fill in all required fields
            }
        });
    }
    
    // Transaction Form
    const transactionForm = document.getElementById('transactionForm');
    if (transactionForm) {
        transactionForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (validateForm(this)) {
                handleTransaction(e);
            } else {
                // Please fill in all required fields
            }
        });
        
        // Show/hide MPIN field based on transaction type
        const transactionTypeSelect = document.getElementById('transactionType');
        const mpinGroup = document.getElementById('mpinGroup');
        const mpinInput = document.getElementById('transactionMpin');
        
        if (transactionTypeSelect && mpinGroup && mpinInput) {
            transactionTypeSelect.addEventListener('change', function() {
                if (this.value === 'withdrawal') {
                    mpinGroup.style.display = 'block';
                    mpinInput.required = true;
                } else {
                    mpinGroup.style.display = 'none';
                    mpinInput.required = false;
                    mpinInput.value = '';
                }
            });
            
            // Restrict MPIN input to numbers only
            mpinInput.addEventListener('input', function(e) {
                e.target.value = e.target.value.replace(/[^0-9]/g, '');
            });
        }
    }
    
    // Transfer Form
    const transferForm = document.getElementById('transferForm');
    if (transferForm) {
        transferForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (validateForm(this)) {
                handleTransfer(e);
            } else {
                // Please fill in all required fields
            }
        });
        
        // Restrict MPIN input to numbers only
        const transferMpinInput = document.getElementById('transferMpin');
        if (transferMpinInput) {
            transferMpinInput.addEventListener('input', function(e) {
                e.target.value = e.target.value.replace(/[^0-9]/g, '');
            });
        }
    }
}

// Statement Modal Functions
function openStatementModal() {
    const modal = document.getElementById('statementModal');
    if (modal) {
        modal.style.display = 'block';
        // Focus on the input field
        setTimeout(() => {
            const input = document.getElementById('statementAccountNumber');
            if (input) input.focus();
        }, 100);
    }
}

function closeStatementModal() {
    const modal = document.getElementById('statementModal');
    if (modal) {
        modal.style.display = 'none';
        // Reset the form
        const form = document.getElementById('statementForm');
        if (form) form.reset();
    }
}

// Download statement function
function downloadStatement(event) {
    event.preventDefault();
    
    const form = event.target;
    const accountNumber = form.accountNumber.value.trim();
    
    if (!accountNumber) {
        alert('Please enter an account number');
        return;
    }
    
    // Show loading state
    const downloadBtn = form.querySelector('.statement-btn-download');
    const originalText = downloadBtn.textContent;
    downloadBtn.textContent = 'Downloading...';
    downloadBtn.disabled = true;
    
    // Create download URL
    const downloadUrl = `http://localhost:8080/api/accounts/${encodeURIComponent(accountNumber)}/transactions/export`;
    
    // Create a temporary link element to trigger download
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = `statement_${accountNumber}.xlsx`;
    
    // Add link to DOM, click it, then remove it
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    // Reset button state
    setTimeout(() => {
        downloadBtn.textContent = originalText;
        downloadBtn.disabled = false;
        closeStatementModal();
    }, 1000);
}

// Custom Notification Functions
function showNotification(message, type = 'info', title = 'Notification') {
    const modal = document.getElementById('notificationModal');
    const icon = document.getElementById('notificationIcon');
    const titleEl = document.getElementById('notificationTitle');
    const messageEl = document.getElementById('notificationMessage');
    
    if (!modal) return;
    
    // Set notification type and styling
    modal.className = `notification-modal ${type}`;
    
    // Set icon based on type
    const icons = {
        error: '❌',
        success: '✅',
        warning: '⚠️',
        info: 'ℹ️'
    };
    
    icon.textContent = icons[type] || icons.info;
    titleEl.textContent = title;
    messageEl.textContent = message;
    
    // Show modal
    modal.style.display = 'block';
    
    // Auto-close after 5 seconds for non-error notifications
    if (type !== 'error') {
        setTimeout(() => {
            closeNotification();
        }, 5000);
    }
}

function closeNotification() {
    const modal = document.getElementById('notificationModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Test notification system (for demonstration)
function testNotifications() {
    showNotification('This is a success notification!', 'success', 'Success');
    setTimeout(() => {
        showNotification('This is a warning notification!', 'warning', 'Warning');
    }, 2000);
    setTimeout(() => {
        showNotification('This is an error notification!', 'error', 'Error');
    }, 4000);
    setTimeout(() => {
        showNotification('This is an info notification!', 'info', 'Information');
    }, 6000);
}

// Delete Account Modal Functions
function openDeleteAccountModal() {
    const modal = document.getElementById('deleteAccountModal');
    if (modal) {
        modal.style.display = 'block';
        // Focus on the account number input field
        setTimeout(() => {
            const input = document.getElementById('deleteAccountNumber');
            if (input) input.focus();
        }, 100);
    }
}

function closeDeleteAccountModal() {
    const modal = document.getElementById('deleteAccountModal');
    if (modal) {
        modal.style.display = 'none';
        // Reset the form
        const form = document.getElementById('deleteAccountForm');
        if (form) form.reset();
    }
}

// Delete account function
function deleteAccount(event) {
    event.preventDefault();
    
    const form = event.target;
    const accountNumber = form.accountNumber.value.trim();
    const password = form.password.value.trim();
    
    if (!accountNumber || !password) {
        showNotification('Please enter both account number and password.', 'error', 'Missing Information');
        return;
    }
    
    // Show loading state
    const deleteBtn = form.querySelector('.delete-btn-confirm');
    const originalText = deleteBtn.textContent;
    deleteBtn.textContent = 'Deleting...';
    deleteBtn.disabled = true;
    
    // Create delete request data
    const deleteData = {
        accountNumber: accountNumber,
        password: password
    };
    
    fetch('http://localhost:8080/api/accounts/delete', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(deleteData)
    })
    .then(async res => {
        const dataText = await res.text();
        if (!res.ok) {
            if (res.status === 401) {
                throw new Error('Incorrect Password Please try again');
            } else if (res.status === 404) {
                throw new Error('Account not found. Please check the account number.');
            } else {
                throw new Error(dataText || 'Account deletion failed');
            }
        }
        return dataText ? JSON.parse(dataText) : { message: 'Account deleted successfully' };
    })
    .then((response) => {
        // Show success notification
        showNotification('Account deleted successfully!', 'success', 'Account Deleted');
        
        // Close modal and reset form
        closeDeleteAccountModal();
        
        // Refresh accounts list
        loadAccounts();
    })
    .catch(err => {
        console.error('Account deletion failed:', err);
        const errorMessage = err.message || 'Unknown error';
        if (errorMessage.includes('password') || errorMessage.includes('Password')) {
            showNotification(errorMessage, 'error', 'Wrong Password');
        } else {
            showNotification('Account deletion failed: ' + errorMessage, 'error', 'Deletion Failed');
        }
    })
    .finally(() => {
        // Reset button state
        deleteBtn.textContent = originalText;
        deleteBtn.disabled = false;
    });
}

// Close modals when clicking outside
window.onclick = function(event) {
    const statementModal = document.getElementById('statementModal');
    const notificationModal = document.getElementById('notificationModal');
    const deleteAccountModal = document.getElementById('deleteAccountModal');
    
    if (event.target === statementModal) {
        closeStatementModal();
    }
    if (event.target === notificationModal) {
        closeNotification();
    }
    if (event.target === deleteAccountModal) {
        closeDeleteAccountModal();
    }
}
