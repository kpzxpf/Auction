let currentPage = 0;
let isLoading = false;
let selectedCategoryName = 'all';
let hasMore = true;

document.addEventListener('DOMContentLoaded', async () => {
    await loadCategories();
    loadAuctionLots();
    updateNavigation();
    window.addEventListener('scroll', handleScroll);
    document.getElementById('categoryList').addEventListener('click', handleCategorySelect);
});

async function loadCategories() {
    const userId = localStorage.getItem('userId');
    try {
        const response = await fetch('http://localhost:8080/categories', {
            headers: userId ? { 'X-User-Id': userId } : {}
        });
        const categories = await response.json();
        const categoryList = document.getElementById('categoryList');
        categoryList.innerHTML = '<li><a class="dropdown-item" href="#" data-category-name="all">Все категории</a></li>';
        categories.forEach(category => {
            const li = document.createElement('li');
            li.innerHTML = `<a class="dropdown-item" href="#" data-category-name="${category.name}">${category.name}</a>`;
            categoryList.appendChild(li);
        });
    } catch (error) {
        console.error('Ошибка:', error);
    }
}

async function loadAuctionLots() {
    if (isLoading || !hasMore) return;
    isLoading = true;
    document.getElementById('loading').style.display = 'block';

    const userId = localStorage.getItem('userId');
    try {
        const url = new URL('http://localhost:8080/feed');
        url.searchParams.append('page', currentPage);
        url.searchParams.append('size', 9);
        if (selectedCategoryName !== 'all') url.searchParams.append('categoryName', selectedCategoryName);

        const response = await fetch(url, {
            headers: userId ? { 'X-User-Id': userId } : {}
        });
        const lots = await response.json();

        if (lots.length < 9) hasMore = false;
        if (lots.length === 0 && currentPage === 0) {
            document.getElementById('lot-grid').innerHTML = '<p class="text-center">Нет доступных лотов</p>';
            return;
        }

        displayAuctionLots(lots);
        currentPage++;
    } catch (error) {
        console.error('Ошибка:', error);
    } finally {
        isLoading = false;
        document.getElementById('loading').style.display = 'none';
    }
}

function displayAuctionLots(lots) {
    const lotGrid = document.getElementById('lot-grid');
    lots.forEach(lot => {
        const col = document.createElement('div');
        col.className = 'col';
        col.innerHTML = `
            <div class="card h-100 shadow-sm">
                <img src="${lot.imageUrls?.[0] || 'images/banner.jpg'}" class="card-img-top" alt="${lot.title}" style="height: 200px; object-fit: cover;">
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
                    <small class="text-muted">Окончание: ${new Date(lot.endTime).toLocaleDateString()}</small>
                </div>
            </div>
        `;
        lotGrid.appendChild(col);
    });
}

function handleCategorySelect(event) {
    event.preventDefault();
    const categoryItem = event.target.closest('[data-category-name]');
    if (!categoryItem) return;

    selectedCategoryName = categoryItem.dataset.categoryName;
    currentPage = 0;
    hasMore = true;
    document.getElementById('lot-grid').innerHTML = '';
    document.getElementById('categoryDropdown').textContent = categoryItem.textContent;
    loadAuctionLots();
}

function handleScroll() {
    const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
    if (scrollTop + clientHeight >= scrollHeight - 100) loadAuctionLots();
}

function updateNavigation() {
    const userId = localStorage.getItem('userId');
    const profileLink = document.getElementById('profileLink');
    const loginLink = document.getElementById('loginLink');
    const registerLink = document.getElementById('registerLink');

    if (userId) {
        profileLink.style.display = 'block';
        loginLink.style.display = 'none';
        registerLink.style.display = 'none';

    } else {
        profileLink.style.display = 'none';
        loginLink.style.display = 'block';
        registerLink.style.display = 'block';
    }
}