import {fetchFromServer, showAlert, showElement, hideElement, toggleSpinner, insertAfter} from "./util/util.js";

//globals
let displayingAllProducts = false;
let currentPage = 0;
let limit = 20; //displayed products per search
let loadedProducts = [];
let currentlyDisplayedProducts = [];
window.onload = function () {
    toggleProductSpinner(true);
    searchProducts('');
    document.addEventListener('error', function (e) {
        const img = e.target;
        if (img.tagName === 'IMG' && img.classList.contains('product-img')) {
            img.src = `${storeData.imgPath}/fallback.svg`;
            img.onerror = null; // prevent infinite loop
        }
    }, true);
    let searchButton = document.getElementById('searchButton');
    searchButton.addEventListener('click', function() {
        let query = document.getElementById('query')?.value;
        currentPage = 0;
        toggleProductSpinner(true);
        searchProducts(query);
    });
    let loadMoreProducts = document.getElementById('loadMoreProducts');
    loadMoreProducts.addEventListener('click', function() {
       currentPage++;
       toggleLoadMoreProductsSpinner(true);
       searchProducts('');
    });

}

function searchProducts(query) {
    const params = new URLSearchParams({
        query: query,
        page: currentPage
    });
    let searchContainer = document.getElementById('searchContainer');
    const url = `${searchContainer.dataset.searchProductsUrl}?${params}`;
    fetchFromServer(url, handleSearchProductsResponse, handleErrorResponse);
}

function handleSearchProductsResponse(products) {
    let cleanSearch = currentPage === 0;
    if (cleanSearch) {
        loadedProducts.length = 0;
        currentlyDisplayedProducts.length = 0;
    }
    loadedProducts.push(...products);
    currentlyDisplayedProducts.push(...products);
    displayingAllProducts = products.length < limit;
    //handle filtering here somewhere hopefully ...
    displayProducts(products, cleanSearch);
}

function handleErrorResponse(error) {
    hideAllSpinners();
    showAlert('#alertWrapper', 'danger', error.message || 'An internal error occurred and the operation could not be completed!');
}

function displayProducts(products, cleanSearch) {
    hideAllSpinners();
    let productContainer = document.getElementById('productContainer');
    let noProductsFound = document.getElementById('noProductsFound');
    cleanSearch && clearDisplayedProducts(productContainer);
    if (!products || products.length === 0 && currentlyDisplayedProducts.length === 0) {
        showElement(noProductsFound);
        return;
    }
    hideElement(noProductsFound);
    let productPlaceholderHTML = getProductPlaceholderHTML();
    products.forEach(product => {
        let placeholder = productPlaceholderHTML.cloneNode(true);
        populatePlaceholderHTML(placeholder, product);
        productContainer.append(placeholder);
    });
    let loadMoreProducts = document.getElementById('loadMoreWrapper');
    if (displayingAllProducts) {
        hideElement(loadMoreProducts);
    } else {
        showElement(loadMoreProducts);
    }
}

function clearDisplayedProducts(productContainer) {
    let spinner = productContainer.firstElementChild;
    productContainer.innerHTML = '';
    productContainer.append(spinner);
}

function getProductPlaceholderHTML() {
    let productDiv = Object.assign(document.createElement('div'), {
        className: 'col',
        id: 'productContentContainer'
    });
    let contentDiv = Object.assign(document.createElement('div'), {
        className: 'card h-100 shadow-sm border-0'
    });
    let imgDiv = Object.assign(document.createElement('div'), {
        className: 'product-img-wrapper bg-secondary bg-opacity-10 d-flex align-items-center justify-content-center text-muted',
        style: 'height: 200px;',
    });
    let descriptionDiv = Object.assign(document.createElement('div'), {
        className: 'card-body d-flex flex-column',
        id: 'descriptionDiv'
    });
    contentDiv.append(imgDiv);
    contentDiv.append(descriptionDiv);
    productDiv.append(contentDiv);
    return productDiv;
}

function populatePlaceholderHTML(placeholder, product) {
    let descriptionDiv = placeholder.querySelector('#descriptionDiv');
    let imgDiv = placeholder.querySelector('.product-img-wrapper');
    let productImg = Object.assign(document.createElement('img'), {
        src: product.imageUrl,
        className: 'img-fluid w-100 h-100 product-img',
        alt: storeData.productImage,
    });
    imgDiv.append(productImg);
    let productName = Object.assign(document.createElement('h5'), {
        className: 'card-title text-truncate',
        textContent: product.name
    });
    descriptionDiv.append(productName);
    let productDescription = Object.assign(document.createElement('span'), {
        className: 'card-text text-muted small flex-grow-1 text-truncate product-description',
        textContent: product.displayedDescription
    });
    descriptionDiv.append(productDescription);
    let productPrice = Object.assign(document.createElement('h4'), {
        className: 'text-primary mb-3 hideable',
        textContent: `${storeData.currency}${product.price}`
    });
    let addToCartButton =  Object.assign(document.createElement('button'), {
        className: 'btn btn-outline-dark w-100 rounded-pill hideable',
        textContent: 'Add to Cart'
    });
    descriptionDiv.append(productPrice);
    descriptionDiv.append(addToCartButton);
    displayToggleDescriptionElement(descriptionDiv, product);
}

function toggleProductSpinner(display) {
    toggleSpinner(document.getElementById('productContainer'), display);
}

function toggleLoadMoreProductsSpinner(display) {
    toggleSpinner(document.getElementById('loadMoreProducts'), display);
}

function hideAllSpinners() {
    toggleProductSpinner(false);
    toggleLoadMoreProductsSpinner(false);
}

function displayToggleDescriptionElement(descriptionDiv, product) {
    let toggleDescriptionElement = Object.assign(document.createElement('a'), {
        className: 'text-decoration-none toggle-description mb-3',
        textContent: storeData.seeMore,
        role: 'button'
    });
    Object.assign(toggleDescriptionElement.dataset, {
        bsToggle: 'modal',
        bsTarget: '#descriptionModal'
    });
    toggleDescriptionElement.addEventListener('click', (e) => toggleProductDescription(e, product));
    let productDescription = descriptionDiv.querySelector('.product-description');
    insertAfter(productDescription, toggleDescriptionElement);
}

function toggleProductDescription(e, product) {
    let descriptionModal = document.getElementById('descriptionModal');
    let modalTitle = descriptionModal.querySelector('.modal-title');
    let modalBody = descriptionModal.querySelector('.modal-body');
    modalTitle.textContent = product.name;
    modalBody.textContent = product.displayedDescription;
}
