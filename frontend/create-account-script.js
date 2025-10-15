// Account Creation functionality
document.addEventListener('DOMContentLoaded', function() {
    initializeAccountCreation();
    setupFormHandlers();
    updateProgressLine();
});

// Initialize account addition
function initializeAccountCreation() {
    console.log('Account addition initialized');
    
    // Set current step
    window.currentStep = 1;
    
    // Initialize form data storage
    window.formData = {
        personalDetails: {},
        verification: {},
        kyc: {},
        password: {}
    };
    
    // Determine navigation context based on referrer
    initializeNavigationContext();
    
    // Ensure step 1 is visible
    const step1 = document.getElementById('step-1');
    if (step1) {
        step1.classList.add('active');
        step1.style.display = 'block';
        step1.style.opacity = '1';
        step1.style.transform = 'none';
    }
    
    // Hide other steps
    const otherSteps = document.querySelectorAll('.form-step:not(#step-1)');
    otherSteps.forEach(step => {
        step.classList.remove('active');
        step.style.display = 'none';
    });
}

// Initialize navigation context based on referrer and URL parameters
function initializeNavigationContext() {
    // Check URL parameters first (more reliable)
    const urlParams = new URLSearchParams(window.location.search);
    const source = urlParams.get('from');
    
    if (source === 'dashboard') {
        window.navigationContext = 'dashboard';
        updateBackButton('dashboard');
        console.log('Navigation context set to: dashboard (from URL parameter)');
        return;
    }
    
    // Fallback to referrer detection
    const referrer = document.referrer;
    console.log('Referrer:', referrer);
    
    // Determine if user came from dashboard or login
    if (referrer.includes('dashboard.html') || referrer.includes('dashboard')) {
        window.navigationContext = 'dashboard';
        updateBackButton('dashboard');
    } else if (referrer.includes('index.html') || referrer.includes('login') || referrer === '') {
        window.navigationContext = 'login';
        updateBackButton('login');
    } else {
        // Default to login if referrer is unclear
        window.navigationContext = 'login';
        updateBackButton('login');
    }
    
    console.log('Navigation context set to:', window.navigationContext);
}

// Update success button based on context
function updateSuccessButton() {
    const successButton = document.querySelector('#step-5 .btn-primary');
    if (successButton) {
        if (window.navigationContext === 'dashboard') {
            successButton.textContent = 'Go to Dashboard';
        } else {
            successButton.textContent = 'Go to Login';
        }
    }
}

// Update back button based on context
function updateBackButton(context) {
    const backButton = document.querySelector('.back-btn');
    if (backButton) {
        if (context === 'dashboard') {
            backButton.textContent = '← Back to Dashboard';
        } else {
            backButton.textContent = '← Back to Login';
        }
    }
}

// Setup form handlers
function setupFormHandlers() {
    // Personal Details Form
    const personalForm = document.getElementById('personalDetailsForm');
    if (personalForm) {
        personalForm.addEventListener('input', function() {
            validateStep(1);
            updateNextButton(1);
        });
    }
    
    // Verification Form
    const verificationForm = document.getElementById('verificationForm');
    if (verificationForm) {
        verificationForm.addEventListener('input', function() {
            validateStep(2);
            updateNextButton(2);
        });
    }
    
    // KYC Form
    const kycForm = document.getElementById('kycForm');
    if (kycForm) {
        kycForm.addEventListener('input', function() {
            validateStep(3);
            updateNextButton(3);
        });
    }
    
    // Password Form
    const passwordForm = document.getElementById('passwordForm');
    if (passwordForm) {
        passwordForm.addEventListener('input', function() {
            validateStep(4);
            updateNextButton(4);
        });
    }
    
    // Setup Previous button handlers
    setupPreviousButtons();
    
    // Initial button state
    updateNextButton(1);
}

// Setup Previous button handlers
function setupPreviousButtons() {
    // Find all Previous buttons and ensure they're properly connected
    const previousButtons = document.querySelectorAll('button[onclick*="prevStep"]');
    console.log('Found Previous buttons:', previousButtons.length);
    
    previousButtons.forEach((button, index) => {
        console.log(`Previous button ${index + 1}:`, button.textContent, button.onclick);
        
        // Ensure the button is clickable and has proper styling
        button.style.cursor = 'pointer';
        button.disabled = false;
        
        // Add click event listener as backup
        button.addEventListener('click', function(e) {
            console.log('Previous button clicked:', button.textContent);
            // The onclick attribute should handle the navigation
        });
    });
}

// Navigate to next step
function nextStep(stepNumber) {
    console.log('Moving to step:', stepNumber);
    
    // Validate current step before proceeding
    if (!validateCurrentStep()) {
        showCustomAlert('Please fill in all required fields before proceeding.');
        return;
    }
    
    // Special validation for step 2 (Verification)
    if (window.currentStep === 2 && stepNumber === 3) {
        if (!validateOTP()) {
            showCustomAlert('Please enter a valid OTP before proceeding.');
            return;
        }
    }
    
    // Special validation for step 4 (Password)
    if (window.currentStep === 4 && stepNumber === 5) {
        if (!validatePasswords()) {
            showCustomAlert('Please ensure passwords match and MPIN is exactly 6 digits.');
            return;
        }
    }
    
    // Save current step data
    saveCurrentStepData();
    
    // Hide current step
    const currentStepElement = document.querySelector('.form-step.active');
    if (currentStepElement) {
        currentStepElement.classList.remove('active');
        currentStepElement.style.display = 'none';
    }
    
    // Show next step
    const nextStepElement = document.getElementById(`step-${stepNumber}`);
    if (nextStepElement) {
        nextStepElement.classList.add('active');
        nextStepElement.style.display = 'block';
        nextStepElement.style.opacity = '1';
        nextStepElement.style.transform = 'none';
    }
    
    // Update progress line
    updateProgressLine(stepNumber);
    
    // Update current step
    window.currentStep = stepNumber;
    
    // Special handling for step 2
    if (stepNumber === 2) {
        displayEmailFromStep1();
        updateNextButton(2);
    }
    
    // Special handling for step 4 (Password)
    if (stepNumber === 4) {
        generateAccountNumber();
        setupPasswordValidation();
    }
    
    // Update button state for new step
    updateNextButton(stepNumber);
    
    // If moving to step 5, generate account details and update success button
    if (stepNumber === 5) {
        generateAccountDetails();
        updateSuccessButton();
    }
}

// Navigate to previous step
function prevStep(stepNumber) {
    console.log('Moving to previous step:', stepNumber, 'from current step:', window.currentStep);
    
    // Hide current step
    const currentStepElement = document.querySelector('.form-step.active');
    if (currentStepElement) {
        console.log('Hiding current step:', currentStepElement.id);
        currentStepElement.classList.remove('active');
        currentStepElement.style.display = 'none';
    }
    
    // Show previous step
    const prevStepElement = document.getElementById(`step-${stepNumber}`);
    if (prevStepElement) {
        console.log('Showing previous step:', prevStepElement.id);
        prevStepElement.classList.add('active');
        prevStepElement.style.display = 'block';
        prevStepElement.style.opacity = '1';
        prevStepElement.style.transform = 'none';
    } else {
        console.error('Previous step element not found:', `step-${stepNumber}`);
    }
    
    // Update progress line
    updateProgressLine(stepNumber);
    
    // Update current step
    window.currentStep = stepNumber;
    console.log('Current step updated to:', window.currentStep);
    
    // Update button state for the new step
    updateNextButton(stepNumber);
    
    // Special handling for step 2 (Verification)
    if (stepNumber === 2) {
        displayEmailFromStep1();
    }
    
    // Special handling for step 4 (Password)
    if (stepNumber === 4) {
        generateAccountNumber();
        setupPasswordValidation();
    }
}

// Validate current step
function validateCurrentStep() {
    const currentStepElement = document.querySelector('.form-step.active');
    if (!currentStepElement) return false;
    
    const requiredFields = currentStepElement.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (field.type === 'checkbox') {
            if (!field.checked) {
                isValid = false;
                field.style.borderColor = '#e74c3c';
            } else {
                field.style.borderColor = '#e0e0e0';
            }
        } else if (field.type === 'file') {
            if (!field.files || field.files.length === 0) {
                isValid = false;
                field.style.borderColor = '#e74c3c';
            } else {
                field.style.borderColor = '#e0e0e0';
            }
        } else {
            if (!field.value.trim()) {
                isValid = false;
                field.style.borderColor = '#e74c3c';
            } else {
                field.style.borderColor = '#e0e0e0';
            }
        }
    });
    
    return isValid;
}

// Validate specific step
function validateStep(stepNumber) {
    const stepElement = document.getElementById(`step-${stepNumber}`);
    if (!stepElement) return false;
    
    const requiredFields = stepElement.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (field.type === 'checkbox') {
            if (!field.checked) {
                isValid = false;
            }
        } else if (field.type === 'file') {
            if (!field.files || field.files.length === 0) {
                isValid = false;
            }
        } else if (field.id === 'initialDeposit') {
            // Special validation for initial deposit
            const depositAmount = parseFloat(field.value);
            if (!field.value.trim() || isNaN(depositAmount) || depositAmount < 10000) {
                isValid = false;
                field.style.borderColor = '#e74c3c';
            } else {
                field.style.borderColor = '#e0e0e0';
            }
        } else if (field.id === 'password' || field.id === 'confirmPassword') {
            // Special validation for password fields
            if (!validatePasswords(false)) {
                isValid = false;
            }
        } else {
            if (!field.value.trim()) {
                isValid = false;
            }
        }
    });
    
    // Update progress step
    const progressStep = document.querySelector(`[data-step="${stepNumber}"]`);
    if (progressStep) {
        if (isValid) {
            progressStep.classList.add('completed');
        } else {
            progressStep.classList.remove('completed');
        }
    }
    
    return isValid;
}

// Update next button styling based on form validation
function updateNextButton(stepNumber) {
    const stepElement = document.getElementById(`step-${stepNumber}`);
    if (!stepElement) return;
    
    const nextButton = stepElement.querySelector('.btn-primary');
    if (!nextButton) return;
    
    const isValid = validateStep(stepNumber);
    
    if (isValid) {
        // Enable button with colored styling
        nextButton.classList.remove('btn-disabled');
        nextButton.classList.add('btn-enabled');
        nextButton.disabled = false;
    } else {
        // Disable button with black and white styling
        nextButton.classList.remove('btn-enabled');
        nextButton.classList.add('btn-disabled');
        nextButton.disabled = true;
    }
}

// Save current step data
function saveCurrentStepData() {
    const currentStepElement = document.querySelector('.form-step.active');
    if (!currentStepElement) return;
    
    const formData = new FormData(currentStepElement.querySelector('form'));
    const stepData = {};
    
    for (let [key, value] of formData.entries()) {
        stepData[key] = value;
    }
    
    // Store data based on current step
    switch (window.currentStep) {
        case 1:
            window.formData.personalDetails = stepData;
            break;
        case 2:
            window.formData.verification = stepData;
            break;
        case 3:
            window.formData.kyc = stepData;
            break;
    }
    
    console.log('Step data saved:', stepData);
}

// Update progress line
function updateProgressLine(currentStep = window.currentStep) {
    const progressSteps = document.querySelectorAll('.progress-step');
    const progressLine = document.querySelector('.progress-line::after');
    
    progressSteps.forEach((step, index) => {
        const stepNumber = index + 1;
        
        // Remove all classes
        step.classList.remove('active', 'completed', 'disabled');
        
        if (stepNumber < currentStep) {
            // Completed steps
            step.classList.add('completed');
        } else if (stepNumber === currentStep) {
            // Current step
            step.classList.add('active');
        } else {
            // Future steps
            step.classList.add('disabled');
        }
    });
    
    // Update progress line width
    const progressAfter = document.querySelector('.progress-line::after');
    if (progressAfter) {
        const progressWidth = ((currentStep - 1) / 3) * 100;
        progressAfter.style.width = `${progressWidth}%`;
    }
}

// Generate account details for step 4
function generateAccountDetails() {
    console.log('Generating account details...');
    
    // Generate random account number
    const accountNumber = 'ACC-' + new Date().getFullYear() + '-' + Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    
    // Get account type from form data
    const accountType = window.formData.kyc.accountType || 'Savings Account';
    const initialDeposit = window.formData.kyc.initialDeposit || '0.00';
    
    // Update the display
    const accountNumberElement = document.getElementById('generatedAccountNumber');
    const accountTypeElement = document.getElementById('generatedAccountType');
    const balanceElement = document.getElementById('generatedBalance');
    
    if (accountNumberElement) {
        accountNumberElement.textContent = accountNumber;
    }
    
    if (accountTypeElement) {
        accountTypeElement.textContent = accountType.charAt(0).toUpperCase() + accountType.slice(1) + ' Account';
    }
    
    if (balanceElement) {
        balanceElement.textContent = `₹${parseFloat(initialDeposit).toFixed(2)}`;
    }
    
    // Store generated account number
    window.generatedAccountNumber = accountNumber;
}

// Go back based on navigation context
function goBack() {
    showCustomConfirm('Are you sure you want to go back? All progress will be lost.', function(result) {
        if (result) {
            // Navigate based on context
            if (window.navigationContext === 'dashboard') {
                window.location.href = 'dashboard.html';
            } else {
                window.location.href = 'index.html';
            }
        }
    });
}

// Go to appropriate page after account addition
function goToDashboard() {
    console.log('Submitting account to backend:', window.generatedAccountNumber);

    // Aggregate data from steps
    const pd = window.formData.personalDetails || {};
    const kyc = window.formData.kyc || {};

    // Fallback helpers to read directly from the DOM if anything is missing
    const getById = (id) => {
        const el = document.getElementById(id);
        return el ? el.value : '';
    };

    // Normalize phone: keep digits only, send only if exactly 10
    const rawPhone = (pd.phone || getById('phone') || '').toString();
    const normalizedPhone = rawPhone.replace(/\D/g, '');

    const payload = {
        nameOnAccount: pd.nameOnAccount || getById('nameOnAccount'),
        accountHolderName: pd.accountHolderName || getById('accountHolderName'),
        email: pd.email || getById('email'),
        bankName: 'AXIS Bank',
        branch: pd.branch || getById('branch') || 'Main Branch',
        accountType: (pd.accountType || kyc.accountType || getById('accountType') || 'savings').toString().toLowerCase(),
        balance: parseFloat(kyc.initialDeposit || getById('initialDeposit') || '0'),
        savingAmount: parseFloat(kyc.savingAmount || kyc.initialDeposit || getById('initialDeposit') || '0'),
        ifscCode: kyc.ifsc || kyc.ifscCode || 'TEST0000001',
        accountNumber: window.generatedAccountNumber,
        mpin: getById('mpin')
    };

    if (normalizedPhone && normalizedPhone.length === 10) {
        payload.phoneLinked = normalizedPhone;
    }

    // Basic validation
    if (!payload.email || !payload.nameOnAccount) {
        showCustomAlert('Please complete required details before submitting.', 'error');
        return;
    }

    // Send to backend
    fetch('http://localhost:8080/api/accounts/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(async res => {
        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || 'Request failed');
        }
        return res.json();
    })
    .then(() => {
        // On success, navigate to dashboard and show success
        const redirectUrl = 'dashboard.html';
        showCustomAlert('Account added successfully! Redirecting to dashboard...', 'success');
        window.location.href = redirectUrl;
    })
    .catch(err => {
        console.error('Failed to create account:', err);
        const hint = ' Please confirm that your Spring Boot server is running on http://localhost:8080 and MySQL is reachable.';
        showCustomAlert('Failed to create account. ' + (err.message || '') + hint, 'error');
    });
}

// Add some interactive animations
document.addEventListener('DOMContentLoaded', function() {
    // Animate progress steps on load
    const progressSteps = document.querySelectorAll('.progress-step');
    progressSteps.forEach((step, index) => {
        step.style.opacity = '0';
        step.style.transform = 'translateY(20px)';
        step.style.transition = 'all 0.3s ease';
        
        setTimeout(() => {
            step.style.opacity = '1';
            step.style.transform = 'translateY(0)';
        }, index * 100);
    });
    
    // Ensure the active form step is visible
    const activeStep = document.querySelector('.form-step.active');
    if (activeStep) {
        activeStep.style.display = 'block';
        activeStep.style.opacity = '1';
        activeStep.style.transform = 'none';
    }
});

// Add keyboard navigation
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        // Go back to dashboard
        goBack();
    }
});

// Add form validation styling
function addValidationStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .form-group input:invalid,
        .form-group select:invalid,
        .form-group textarea:invalid {
            border-color: #e74c3c;
        }
        
        .form-group input:valid,
        .form-group select:valid,
        .form-group textarea:valid {
            border-color: #27ae60;
        }
        
        .progress-step:hover:not(.disabled) {
            transform: translateY(-2px);
        }
        
        .progress-step:hover:not(.disabled) .step-circle {
            transform: scale(1.05);
        }
    `;
    document.head.appendChild(style);
}

// Display email from step 1 in step 2
function displayEmailFromStep1() {
    const email = window.formData.personalDetails.email;
    const displayEmailElement = document.getElementById('displayEmail');
    
    if (displayEmailElement && email) {
        displayEmailElement.textContent = email;
    }
}

// Send OTP function
function sendOTP() {
    const email = window.formData.personalDetails.email;
    const name = window.formData.personalDetails.accountHolderName || 'Customer';
    const sendOtpBtn = document.getElementById('sendOtpBtn');
    
    if (!email) {
        showOtpStatus('Please complete Step 1 first.', 'error');
        return;
    }
    
    sendOtpBtn.textContent = 'Sending...';
    sendOtpBtn.disabled = true;
    
    fetch('http://localhost:8080/api/otp/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, name })
    })
    .then(async res => {
        const text = await res.text();
        if (!res.ok) throw new Error(text || 'Failed to send OTP');
        return text;
    })
    .then(() => {
        showOtpStatus(`OTP sent to ${email}. Please check your email.`, 'info');
        sendOtpBtn.textContent = 'Resend OTP';
        sendOtpBtn.disabled = false;
    })
    .catch(err => {
        console.error('OTP send failed:', err);
        showOtpStatus('Failed to send OTP. Please try again.', 'error');
        sendOtpBtn.textContent = 'Send OTP';
        sendOtpBtn.disabled = false;
    });
}

// Validate OTP
function validateOTP() {
    const enteredOTP = document.getElementById('otp').value;
    const email = window.formData.personalDetails.email;
    
    if (!enteredOTP) {
        showOtpStatus('Please enter the OTP.', 'error');
        return false;
    }
    
    return fetch('http://localhost:8080/api/otp/verify', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, otp: enteredOTP })
    })
    .then(res => res.json())
    .then(data => {
        if (data && data.valid) {
            showOtpStatus('OTP verified successfully!', 'success');
            return true;
        }
        showOtpStatus(data.error || 'Invalid OTP. Please try again.', 'error');
        return false;
    })
    .catch(() => {
        showOtpStatus('Verification failed. Please try again.', 'error');
        return false;
    });
}

// Show OTP status
function showOtpStatus(message, type) {
    const otpStatus = document.getElementById('otpStatus');
    if (otpStatus) {
        otpStatus.textContent = message;
        otpStatus.className = `otp-status ${type}`;
        
        // Auto-hide success messages after 3 seconds
        if (type === 'success') {
            setTimeout(() => {
                otpStatus.textContent = '';
                otpStatus.className = 'otp-status';
            }, 3000);
        }
    }
}

// Generate account number
function generateAccountNumber() {
    // Generate a unique account number
    const timestamp = Date.now().toString().slice(-6);
    const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    const accountNumber = `ACC-${timestamp}-${random}`;
    
    // Store the account number
    window.generatedAccountNumber = accountNumber;
    
    // Display the account number
    const displayElement = document.getElementById('displayAccountNumber');
    if (displayElement) {
        displayElement.textContent = accountNumber;
    }
    
    console.log('Account number generated:', accountNumber);
}

// Setup password validation
function setupPasswordValidation() {
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const mpinInput = document.getElementById('mpin');
    const confirmMpinInput = document.getElementById('confirmMpin');
    
    if (passwordInput && confirmPasswordInput && mpinInput && confirmMpinInput) {
        // Add event listeners for real-time validation
        passwordInput.addEventListener('input', validatePasswords);
        confirmPasswordInput.addEventListener('input', validatePasswords);
        mpinInput.addEventListener('input', validatePasswords);
        confirmMpinInput.addEventListener('input', validatePasswords);
        
        // Add input restrictions for MPIN
        mpinInput.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/[^0-9]/g, '');
        });
        confirmMpinInput.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/[^0-9]/g, '');
        });
    }
}

// Validate passwords
function validatePasswords(showStatus = true) {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const mpin = document.getElementById('mpin').value;
    const confirmMpin = document.getElementById('confirmMpin').value;
    const passwordStatus = document.getElementById('passwordStatus');
    
    if (!password && !confirmPassword && !mpin && !confirmMpin) {
        // Clear status if all fields are empty
        if (passwordStatus && showStatus) {
            passwordStatus.textContent = '';
            passwordStatus.className = 'password-status';
        }
        return false;
    }
    
    // Validate password
    if (password.length < 6) {
        if (showStatus) {
            showPasswordStatus('Password must be at least 6 characters long.', 'error');
        }
        return false;
    }
    
    if (password !== confirmPassword) {
        if (showStatus) {
            showPasswordStatus('Passwords do not match.', 'error');
        }
        return false;
    }
    
    // Validate MPIN
    if (mpin.length !== 6 || !/^[0-9]{6}$/.test(mpin)) {
        if (showStatus) {
            showPasswordStatus('MPIN must be exactly 6 digits.', 'error');
        }
        return false;
    }
    
    if (mpin !== confirmMpin) {
        if (showStatus) {
            showPasswordStatus('MPINs do not match.', 'error');
        }
        return false;
    }
    
    if (showStatus) {
        showPasswordStatus('Password and MPIN are valid!', 'success');
    }
    return true;
}

// Show password status
function showPasswordStatus(message, type) {
    const passwordStatus = document.getElementById('passwordStatus');
    if (passwordStatus) {
        passwordStatus.textContent = message;
        passwordStatus.className = `password-status ${type}`;
        
        // Auto-hide success messages after 3 seconds
        if (type === 'success') {
            setTimeout(() => {
                passwordStatus.textContent = '';
                passwordStatus.className = 'password-status';
            }, 3000);
        }
    }
}

// Increment deposit amount by ₹100
function incrementDeposit() {
    const depositInput = document.getElementById('initialDeposit');
    if (depositInput) {
        let currentValue = parseFloat(depositInput.value) || 10000;
        let newValue = currentValue + 100;
        
        // Ensure it doesn't go below minimum
        if (newValue < 10000) {
            newValue = 10000;
        }
        
        depositInput.value = newValue;
        
        // Trigger validation and button update
        validateStep(3);
        updateNextButton(3);
        
        console.log('Deposit incremented to:', newValue);
    }
}

// Decrement deposit amount by ₹100
function decrementDeposit() {
    const depositInput = document.getElementById('initialDeposit');
    if (depositInput) {
        let currentValue = parseFloat(depositInput.value) || 10000;
        let newValue = currentValue - 100;
        
        // Ensure it doesn't go below minimum
        if (newValue < 10000) {
            newValue = 10000;
        }
        
        depositInput.value = newValue;
        
        // Trigger validation and button update
        validateStep(3);
        updateNextButton(3);
        
        console.log('Deposit decremented to:', newValue);
    }
}

// Show custom alert modal
function showCustomAlert(message, type = 'error', title = 'Alert') {
    const modal = document.getElementById('customAlertModal');
    const icon = document.getElementById('customAlertIcon');
    const titleEl = document.getElementById('customAlertTitle');
    const messageEl = document.getElementById('customAlertMessage');
    
    if (!modal) return;
    
    // Set alert type and styling
    modal.className = `custom-alert-modal ${type}`;
    
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
    
    // Auto-close after 5 seconds for non-error alerts
    if (type !== 'error') {
        setTimeout(() => {
            closeCustomAlert();
        }, 5000);
    }
}

function closeCustomAlert() {
    const modal = document.getElementById('customAlertModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Close modal when clicking outside
window.onclick = function(event) {
    const customAlertModal = document.getElementById('customAlertModal');
    if (event.target === customAlertModal) {
        closeCustomAlert();
    }
}

// Show custom confirmation modal
function showCustomConfirm(message, callback) {
    const confirmModal = document.getElementById('confirmModal');
    const confirmMessage = document.getElementById('confirmMessage');
    
    if (confirmModal && confirmMessage) {
        confirmMessage.textContent = message;
        confirmModal.classList.add('show');
        
        // Store callback for later use
        window.confirmCallback = callback;
        
        // Prevent body scroll when modal is open
        document.body.style.overflow = 'hidden';
    }
}

// Close confirmation modal
function closeConfirmModal(result) {
    const confirmModal = document.getElementById('confirmModal');
    
    if (confirmModal) {
        confirmModal.classList.remove('show');
        
        // Restore body scroll
        document.body.style.overflow = 'auto';
        
        // Execute callback with result
        if (window.confirmCallback) {
            window.confirmCallback(result);
            window.confirmCallback = null;
        }
    }
}

// Close modal with Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeAlertModal();
        closeConfirmModal(false);
    }
});

// Initialize validation styles
addValidationStyles();
