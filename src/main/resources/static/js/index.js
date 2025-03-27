let currentPage = 0;
let isLoading = false;
let selectedCategoryId = 'all';
let selectedCategoryName = 'all';
let hasMore = true;

document.addEventListener("DOMContentLoaded", () => {
    loadCategories();
    setupEventListeners();
    loadAuctionLots();
    updateNavigation();
});

function setupEventListeners() {
    window.addEventListener('scroll', handleScroll);
    document.getElementById('logoutButton').addEventListener('click', handleLogout);
    document.getElementById('categoryList').addEventListener('click', handleCategorySelect);
}

async function loadCategories() {
    try {
        const response = await fetch('http://localhost:8080/categories');
        const categories = await response.json();
        populateCategories(categories);
    } catch (error) {
        console.error('Ошибка загрузки категорий:', error);
    }
}

function populateCategories(categories) {
    const categoryList = document.getElementById('categoryList');
    categories.forEach(category => {
        const li = document.createElement('li');
        li.innerHTML = `
            <a class="dropdown-item" href="#" data-category-id="${category.id}" data-category-name="${category.name}">
                ${category.name}
            </a>
        `;
        categoryList.appendChild(li);
    });
}

async function loadAuctionLots() {
    if (isLoading || !hasMore) return;
    isLoading = true;
    showLoading();

    try {
        const url = new URL('http://localhost:8080/feed');
        url.searchParams.append('page', currentPage);
        url.searchParams.append('size', 9);
        if (selectedCategoryId !== 'all') {
            url.searchParams.append('categoryName', selectedCategoryName);
        }

        const response = await fetch(url);
        const lots = await response.json();

        if (lots.length === 0) {
            hasMore = false;
            if (currentPage === 0) showNoLotsMessage();
            return;
        }

        for (const lot of lots) {
            const images = await loadLotImages(lot.id);
            lot.images = images;
        }

        displayAuctionLots(lots);
        currentPage++;
    } catch (error) {
        console.error('Ошибка загрузки лотов:', error);
    } finally {
        isLoading = false;
        hideLoading();
    }
}

async function loadLotImages(lotId) {
    try {
        const response = await fetch(`http://localhost:8080/images/${lotId}`);
        return await response.json();
    } catch (error) {
        console.error('Ошибка загрузки изображений:', error);
        return [];
    }
}

function displayAuctionLots(lots) {
    const lotGrid = document.getElementById('lot-grid');
    lots.forEach(lot => {
        const lotElement = createLotElement(lot);
        lotGrid.appendChild(lotElement);
    });
}

function createLotElement(lot) {
    const col = document.createElement('div');
    col.className = 'col';

    col.innerHTML = `
        <div class="card h-100 shadow-sm">
            <img src="${lot.images?.[0]?.url || 'images/banner.jpg'}" 
                 class="card-img-top" 
                 alt="${lot.title}"
                 style="height: 200px; object-fit: cover;">
            <div class="card-body">
                <h5 class="card-title">${lot.title}</h5>
                <p class="card-text text-muted">${lot.description || ''}</p>
                <div class="d-flex justify-content-between align-items-center">
                    <span class="badge bg-primary">${lot.categoryName || 'Без категории'}</span>
                    <h5 class="text-success">${lot.currentPrice.toFixed(2)}₽</h5>
                </div>
                <a href="lot.html?id=${lot.id}" class="stretched-link"></a>
            </div>
            <div class="card-footer bg-transparent">
                <small class="text-muted">
                    Окончание: ${new Date(lot.endTime).toLocaleDateString()}
                </small>
            </div>
        </div>
    `;
    return col;
}

function handleCategorySelect(event) {
    const categoryItem = event.target.closest('[data-category-id]');
    if (!categoryItem) return;

    selectedCategoryId = categoryItem.dataset.categoryId;
    selectedCategoryName = categoryItem.dataset.categoryName || categoryItem.textContent.trim();

    currentPage = 0;
    hasMore = true;

    document.getElementById('lot-grid').innerHTML = '';
    document.getElementById('categoryDropdown').textContent = selectedCategoryName;

    loadAuctionLots();
}

function handleScroll() {
    const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
    if (scrollTop + clientHeight >= scrollHeight - 100) {
        loadAuctionLots();
    }
}

function updateNavigation() {
    const token = localStorage.getItem('jwtToken');
    const elements = {
        profileLink: document.getElementById('profileLink'),
        logoutButton: document.getElementById('logoutButton'),
        loginLink: document.getElementById('loginLink'),
        registerLink: document.getElementById('registerLink')
    };

    if (token) {
        elements.profileLink.style.display = 'block';
        elements.logoutButton.style.display = 'block';
        elements.loginLink.style.display = 'none';
        elements.registerLink.style.display = 'none';
    } else {
        elements.profileLink.style.display = 'none';
        elements.logoutButton.style.display = 'none';
        elements.loginLink.style.display = 'block';
        elements.registerLink.style.display = 'block';
    }
}

function handleLogout() {
    localStorage.removeItem('jwtToken');
    updateNavigation();
    window.location.reload();
}

function showLoading() {
    document.getElementById('loading').style.display = 'block';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

function showNoLotsMessage() {
    const lotGrid = document.getElementById('lot-grid');
    lotGrid.innerHTML = `
        <div class="col-12 text-center py-5">
            <h4 class="text-muted">Нет доступных лотов в выбранной категории</h4>
        </div>
    `;
}