# Лабораторна робота №4 — RESTful вебсервіси

## Як запустити
1. Відкрийте саме папку `Spring4_Lab4` у IntelliJ IDEA як Maven-проєкт.
2. Використовуйте **JDK 17+** і дочекайтеся завантаження Maven-залежностей.
3. Запустіть `com.example.spring2.Spring2Application` (назва основного Java-класу збережена, щоб не ламати проєкт).
4. Swagger UI: `http://localhost:8080/swagger-ui.html`.
5. OpenAPI JSON: `http://localhost:8080/v3/api-docs`.
6. Старі сторінки: `http://localhost:8080/groups`, `http://localhost:8080/students`.

Якщо порт 8080 зайнятий, зупиніть попередню лабораторну.

## Приклади для Postman

| Метод | URL | Що робить / статус |
|---|---|---|
| GET | `http://localhost:8080/api/groups` | Перелік груп, 200 |
| GET | `http://localhost:8080/api/groups/1` | Одна група, 200 або 404 |
| POST | `http://localhost:8080/api/groups` | Створює групу, 201 |
| PUT | `http://localhost:8080/api/groups/1` | Повна заміна, 200 |
| DELETE | `http://localhost:8080/api/groups/1` | Видаляє групу та студентів, 204 |
| GET | `http://localhost:8080/api/students?lastName=Пли&groupId=1&page=0&size=2` | Фільтр і пагінація, 200 |
| GET | `http://localhost:8080/api/students/1` | Студент, 200 або 404 |
| POST | `http://localhost:8080/api/students` | Створення, 201 |
| PUT | `http://localhost:8080/api/students/1` | Повна заміна, 200 |
| PATCH | `http://localhost:8080/api/students/1` | Часткова зміна, 200 |
| DELETE | `http://localhost:8080/api/students/1` | Видалення, 204 |

### Тіла запитів (Postman → Body → raw → JSON)

**POST /api/groups**
```json
{"name":"ІА-45"}
```

**POST /api/students**
```json
{"firstName":"Олена","lastName":"Іваненко","groupId":1}
```

**PUT /api/students/1**
```json
{"firstName":"Олена","lastName":"Петренко","groupId":2}
```

**PATCH /api/students/1 — RFC 7386**
Задайте в Headers `Content-Type: application/merge-patch+json`.
```json
{"lastName":"Коваленко"}
```

**PATCH /api/students/1 — RFC 6902**
Задайте в Headers `Content-Type: application/json-patch+json`.
```json
[{"op":"replace","path":"/lastName","value":"Коваленко"}]
```

У Postman для PATCH зручно вибирати Body → raw → JSON, а потім перевизначати `Content-Type` у Headers на потрібний MIME тип.

### Очікувані помилки
- `400` — некоректна структура чи дані, порожнє поле, невірна сторінка.
- `404` — відсутній студент або група.
- `409` — невдала операція `test` JSON Patch.
- `405` — HTTP-метод не підтримується.

Успішний POST повертає `201` і заголовок `Location`. Видалення повертає `204` без JSON.

### Важливе
- У сховищі використовуються `ArrayList`: всі додані/змінені записи зникнуть після перезапуску.
- API підтримує обидва формати PATCH (RFC 6902 і RFC 7386) для **плоских полів** ресурсу. Видалення обов'язкового поля через `null` або `remove` дасть `400`.
- Безпекова авторизація не реалізована відповідно до дозволу в методичці.
- Повний опис операцій і можливі статуси є у Swagger, а у Postman слід перевірити їх фактичні відповіді.

## Готова колекція Postman
У Postman натисніть **Import** і виберіть `Spring4_Postman_collection.json`. Колекція містить запити з перевірками HTTP-статусів. Тестові запити змінюють дані, тому запускайте їх на свіжому старті застосунку та в рекомендованому порядку.
