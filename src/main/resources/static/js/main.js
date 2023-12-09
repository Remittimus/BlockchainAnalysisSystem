


function getCookie(name) {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
}

var token = $('input[name="_csrf"]').val();

var header = $('input[name="_csrf_header"]').val();

$('.remove-subscription').click(function() {
    var csrfToken = getCookie("XSRF-TOKEN"); // Получаем токен перед отправкой запроса

    var entityId = $(this).data('id');
    var $button = $(this); // сохраняем контекст кнопки
    $button.parent().parent().hide();
   $.ajax({
        url: '/subscriptions/' + entityId,
        type: 'DELETE',
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token); // Используем 'X-XSRF-TOKEN'
        },
        success: function(result) {
            // обновляем таблицу
            $button.parent().parent().remove();
        },
       error: function(xhr, status, error) {

           $button.parent().parent().show();
           console.log('Произошла ошибка:', error);

       }

    });
});

var loginForm = document.getElementById('loginForm');
if (loginForm) {
    document.getElementById('loginForm').addEventListener('submit', function (event) {
        var isValid = true;
        var message = '';
        // Валидация Email
        var username = document.getElementById('username').value;
        if (username.length === 0) {
            message+='Имя пользователя не должно быть пустым.\n';
            isValid = false;
        }

        // Валидация Пароля
        var password = document.getElementById('password').value;
        if (password.length < 1) {
            message+='Пароль не должен быть пустым.';
            isValid = false;
        }

        if (!isValid) {
            alert(message);
            event.preventDefault();
        }
    });
}

var registerForm = document.getElementById('RegisterForm');
if (registerForm) {
    document.getElementById('RegisterForm').addEventListener('submit', function (event) {
        var isValid = true;
        var message = '';
        // Валидация Email
        var email = document.getElementById('email').value;
        if (!email.match(/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/)) {
            message += 'Некорректный адрес электронной почты.\n';
            isValid = false;
        }

        // Валидация Имени Пользователя
        var username = document.getElementById('username').value;
        if (username.length === 0) {
            message += 'Имя пользователя не должно быть пустым.\n';
            isValid = false;
        }

        // Валидация Пароля
        var password = document.getElementById('password').value;
        if (password.length === 0) {
            message += 'Пароль не должен быть пустым.';
            isValid = false;
        }

        if (!isValid) {
            alert(message);
            event.preventDefault();
        }
    });
}

var subscribeForm = document.getElementById('subscribeForm');
if (subscribeForm) {
    subscribeForm.addEventListener('submit', function(event) {
        var isValid = true;
        var message = '';

        // Валидация адреса
        var address = document.getElementById('address').value;
        if (!address.trim()) {
            message += 'Адрес не должен быть пустым.\n';
            isValid = false;
        }

        // Валидация выбора типа подписки
        var checkboxes = document.querySelectorAll('#subscribeForm input[type="checkbox"]:checked');
        if (checkboxes.length === 0) {
            message += 'Необходимо выбрать хотя бы один тип подписки.\n';
            isValid = false;
        }

        if (!isValid) {
            alert(message);
            event.preventDefault(); // предотвращаем отправку формы, если данные невалидны
        }
    });
}


