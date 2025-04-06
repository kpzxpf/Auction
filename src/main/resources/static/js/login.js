document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    if (!loginForm) {
        console.error('Форма с ID "login-form" не найдена');
        return;
    }

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            console.log('Статус ответа:', response.status);

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Неверные учетные данные');
            }

            const contentType = response.headers.get('Content-Type');
            if (contentType && contentType.includes('application/json')) {
                const userId = await response.json();
                console.log('Полученный userId:', userId);
                if (!userId) throw new Error('ID пользователя не получен от сервера');
                localStorage.setItem('userId', userId);
                console.log('Сохранённый userId:', localStorage.getItem('userId'));
                window.location.href = 'index.html';
            } else {
                throw new Error('Некорректный тип ответа сервера');
            }
        } catch (error) {
            console.error('Ошибка при входе:', error);
            document.getElementById('login-message').innerHTML =
                `<div class="alert alert-danger">${error.message}</div>`;
        }
    });
});