document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const lotId = params.get("id");
    const userId = localStorage.getItem('userId');

    if (!lotId || !userId) {
        alert("Отсутствует идентификатор лота или пользователь не авторизован.");
        window.location.href = "profile.html";
        return;
    }

    await loadLotData(lotId);
    setupFormSubmission(lotId, userId);
});

async function loadLotData(lotId) {
    try {
        const response = await fetch(`http://localhost:8080/lots/${lotId}`);
        if (!response.ok) throw new Error("Лот не найден");

        const lot = await response.json();
        await loadCategories('category', lot.categoryName);
        populateForm(lot);
    } catch (error) {
        alert(error.message);
        window.location.href = "profile.html";
    }
}

function populateForm(lot) {
    document.getElementById("title").value = lot.title;
    document.getElementById("description").value = lot.description;
    document.getElementById("startingPrice").value = lot.startingPrice;
    document.getElementById("startTime").value = lot.startTime;
    document.getElementById("endTime").value = lot.endTime;
    document.getElementById("status").value = lot.status;
}

function setupFormSubmission(lotId, userId) {
    document.getElementById("edit-lot-form").addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = {
            id: lotId,
            title: document.getElementById("title").value,
            description: document.getElementById("description").value,
            startingPrice: document.getElementById("startingPrice").value,
            startTime: document.getElementById("startTime").value,
            endTime: document.getElementById("endTime").value,
            sellerId: userId,
            categoryName: document.getElementById("category").value,
            status: document.getElementById("status").value
        };

        try {
            const response = await fetch(`http://localhost:8080/lots`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) throw new Error("Ошибка обновления лота");

            alert("Лот успешно обновлен!");
            window.location.href = "profile.html";
        } catch (error) {
            console.error(error);
            alert("Ошибка при обновлении лота");
        }
    });
}

async function loadCategories(selectElementId, selectedValue = '') {
    const userId = localStorage.getItem('userId');
    try {
        const response = await fetch('http://localhost:8080/categories', {
            headers: userId ? { 'X-User-Id': userId } : {}
        });

        if (!response.ok) throw new Error('Не удалось загрузить категории');

        const categories = await response.json();
        const select = document.getElementById(selectElementId);
        select.innerHTML = '<option value="">Выберите категорию</option>';

        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category.name;
            option.textContent = category.name;
            if (category.name === selectedValue) {
                option.selected = true;
            }
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Ошибка при загрузке категорий:', error);
    }
}
