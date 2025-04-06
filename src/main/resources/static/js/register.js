document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    if (!registerForm) return;

    registerForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const registerButton = document.getElementById('register-button');
        registerButton.disabled = true;

        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const passwordRepeat = document.getElementById('password-repeat').value;

        if (password !== passwordRepeat) {
            document.getElementById('register-message').innerHTML =
                '<div class="alert alert-danger">Пароли не совпадают</div>';
            registerButton.disabled = false;
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password })
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Ошибка регистрации');
            }

            const userId = await response.json();
            console.log('Полученный userId:', userId);
            if (!userId) throw new Error('ID пользователя не получен от сервера');
            localStorage.setItem('userId', userId);
            console.log('Сохранённый userId:', localStorage.getItem('userId'));
            document.getElementById('register-message').innerHTML =
                '<div class="alert alert-success">Регистрация успешна! Перенаправление...</div>';
            setTimeout(() => window.location.href = 'index.html', 2000);
        } catch (error) {
            document.getElementById('register-message').innerHTML =
                `<div class="alert alert-danger">${error.message}</div>`;
            registerButton.disabled = false;
        }
    });
});