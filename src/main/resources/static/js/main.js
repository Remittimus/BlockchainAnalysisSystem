// var csrfToken = $('meta[name="_csrf"]').attr('content');
//var csrfToken = $('input[name="_csrf"]').val();


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
           // Обработчик ошибки
           $button.parent().parent().show();
           console.log('Произошла ошибка:', error);
           // Дополнительный код, который нужно выполнить при ошибке
       }

    });
});

// function getCookie(name) {
//     var value = "; " + document.cookie;
//     var parts = value.split("; " + name + "=");
//     if (parts.length == 2) return parts.pop().split(";").shift();
// }
//
// var csrfToken = getCookie("XSRF-TOKEN");
//
// $('.remove-subscription').click(function() {
//     var entityId = $(this).data('id');
//     var $button = $(this); // сохраняем контекст кнопки
//
//     $.ajax({
//         url: '/subscriptions/' + entityId,
//         type: 'DELETE',
//         headers: {
//             'X-XSRF-TOKEN': csrfToken,
//             'Content-Type': 'application/x-www-form-urlencoded' // Добавляем этот заголовок
//         },
//         success: function(result) {
//             // обновляем таблицу
//             $button.parent().parent().remove();
//         }
//     });
// });


// function getCookie(name) {
//     var value = "; " + document.cookie;
//     var parts = value.split("; " + name + "=");
//     if (parts.length == 2) return parts.pop().split(";").shift();
// }
// var csrfToken = getCookie("XSRF-TOKEN");
//
//
// $('.remove-subscription').click(function() {
//     var entityId = $(this).data('id');
//     var $button = $(this); // сохраняем контекст кнопки
//
//     $.ajax({
//         url: '/subscriptions/' + entityId,
//         type: 'DELETE',
//         beforeSend: function(xhr) {
//             xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken); // Обрати внимание, что здесь используется 'X-XSRF-TOKEN'
//         },
//         success: function(result) {
//             // обновляем таблицу
//             $button.parent().parent().remove();
//         }
//     });
// });
////////////
// function getCookie(name) {
//     var value = "; " + document.cookie;
//     var parts = value.split("; " + name + "=");
//     if (parts.length == 2) return parts.pop().split(";").shift();
// }
// var csrfToken = getCookie("XSRF-TOKEN");
//
// // Включение CSRF-токена в заголовок запроса AJAX
// $.ajaxSetup({
//     beforeSend: function(xhr) {
//         xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
//     }
// });
// $('.remove-subscription').click(function() {
//     var entityId = $(this).data('id');
//     var $button = $(this); // сохраняем контекст кнопки
//
//     $.ajax({
//         url: '/subscriptions/' + entityId,
//         type: 'DELETE',
//         beforeSend: function(xhr) {
//             xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
//         },
//         success: function(result) {
//             // обновляем таблицу
//             $button.parent().parent().remove();
//         }
//     });
// });








// $('.remove-subscription').click(function() {
//     var entityId = $(this).data('id');
//     $.ajax({
//         url: '/subscriptions/' + entityId,
//         type: 'DELETE',
//         success: function(result) {
//             // обновляем таблицу
//             $(this).parent().parent().remove();
//         }
//     });
// });
// (function ($) {
//     "use strict";
//
//
//     /*==================================================================
//     [ Validate ]*/
//     var input = $('.validate-input .input100');
//
//     $('.validate-form').on('submit',function(){
//         var check = true;
//
//         for(var i=0; i<input.length; i++) {
//             if(validate(input[i]) == false){
//                 showValidate(input[i]);
//                 check=false;
//             }
//         }
//
//         return check;
//     });
//
//
//     $('.validate-form .input100').each(function(){
//         $(this).focus(function(){
//            hideValidate(this);
//         });
//     });
//
//     function validate (input) {
//         if($(input).attr('type') == 'email' || $(input).attr('name') == 'email') {
//             if($(input).val().trim().match(/^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\]?)$/) == null) {
//                 return false;
//             }
//         }
//         else {
//             if($(input).val().trim() == ''){
//                 return false;
//             }
//         }
//     }
//
//     function showValidate(input) {
//         var thisAlert = $(input).parent();
//
//         $(thisAlert).addClass('alert-validate');
//     }
//
//     function hideValidate(input) {
//         var thisAlert = $(input).parent();
//
//         $(thisAlert).removeClass('alert-validate');
//     }
//
//
//
// })(jQuery);