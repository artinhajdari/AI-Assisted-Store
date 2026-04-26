export const fetchFromServer = (serverUrl, successCallback, errorCallback = undefined) => {
    if (!serverUrl || !successCallback) {
        return;
    }
    fetch(serverUrl, {
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error(response.errorMessage);
        })
        .then(data => {
            successCallback(data);
        }).catch(error => {
        console.log(error);
        if (errorCallback) {
            errorCallback(error);
        }
    });
}

export const toggleSpinner = (element, display) => {
    if (!element) {
        return;
    }
    let loadingSpinner = element.querySelector('.loading-spinner');
    if (display) {
        element.classList.add('pe-none');
        loadingSpinner.classList.remove('d-none');
    } else {
        element.classList.remove('pe-none');
        loadingSpinner.classList.add('d-none');
    }
}

export const showElement = (element) => {
    if (!element) {
        return;
    }
    element.classList.remove('d-none');
}

export const hideElement = (element) => {
    if (!element) {
        return;
    }
    element.classList.add('d-none');
}

export const insertAfter = (referenceElement, insertionElement) => {
    if (!referenceElement || !insertionElement) {
        return;
    }
    referenceElement.parentNode.insertBefore(insertionElement, referenceElement.nextSibling);
}

export const showAlert = (wrapperSelector, alertLevel, message) => {
    if (!wrapperSelector) {
        return;
    }
    let wrapperElement = document.querySelector(wrapperSelector);
    if (!wrapperElement) {
        return;
    }
    wrapperElement.innerHTML = `
    <div class="alert alert-${alertLevel} alert-dismissible text-center fade show" role="alert">
      ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  `;
    const alertElement = wrapperElement.firstElementChild;
    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(alertElement);
        bsAlert.close();
    }, 5000);
}