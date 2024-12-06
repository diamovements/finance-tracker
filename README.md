# BUDGETARY - Приложение для отслеживания личных финансов

## _About_

Микросервисное приложение на _Spring Boot_, позволяющее отслеживать личные финансы пользователя. Состоит из трех
микросервисов: сервис пользователя, отвечающий за аутентификацию и регистрацию, сервис уведомлений для отправки сообщений на почту, сервис
финансов, являющийся основным и отвечающий за операции с финансами.

## _Used stack_

- Spring Framework
- Kafka
- Quotes API & Google Mail API
- Docker
- Gradle
- OpenAPI
- Flyway
- JUnit & Mockito
- Prometheus & Grafana
- PostgreSQL

## _Functions_

### Безопасность пользователя

- Смена пароля с рандомным кодом для подтверждения
- Вход и регистрация на основе JWT-токенов доступа и обновления
- Выход из аккаунта

### Отслеживание финансов

- Добавление личных категорий расходов и доходов
- Добавление баланса в личном кабинете
- Добавление лимитов на день/неделю/месяц
- Возможность добавить повторяющиеся платежи (например, оплата проездного или аренды)
- Постановка цели для накопления
- Добавление транзакций (расходы и доходы) в любые категории
- Уведомления на почту при добавлении категорий, лимитов, целей, транзакций и платежей

### Инструкция по запуску 

1. В корневой директории проекта (где находится docker compose) 
запустите команду docker-compose up --build
2. Приложение доступно по адресу ```http://localhost:8082/index.html```
3. Для работы с функционалом необходимо зарегистрироваться или войти (лучше использовать существующую почту для получения уведомлений)

_Метрики доступны по адресу ```http://localhost:3000```_

_Документация доступна по адресу ```http://localhost:8082/swagger-ui/index.html```_

### Примечание

Для корректной работы приложения необходимо создать файл .env в корне проекта и добавить туда следующие секреты:
```
JWT_SECRET_KEY=<ваш jwt ключ>
GOOGLE_MAIL_PASSWORD=<ваш пароль для google mail>
GOOGLE_MAIL_USERNAME=<ваша почта google>
QUOTES_API_KEY=<ваш api ключ>
POSTGRES_USERNAME=<ваше имя пользователя postgresql>
POSTGRES_PASSWORD=<ваш пароль postgresql>
POSTGRES_URL=<ваш адрес БД>
```
API ключ для сервиса цитат можно получить здесь: ```https://api-ninjas.com/api/quotes```

### Демонстрация работы приложения

**Главная страница**

![image](https://github.com/user-attachments/assets/a3926d8d-27be-4d15-b47e-974f386cf35d)

**Страница входа**

![image](https://github.com/user-attachments/assets/939a2878-4e89-4fba-8ba4-7eff2b8549d4)

**Страница регистрации**

![image](https://github.com/user-attachments/assets/69c1302f-b765-4417-b1b6-e2462cf723fc)

**Личный кабинет**

![image](https://github.com/user-attachments/assets/b9ef0400-0188-484c-a5b1-b57116f38d11)

**Лимиты**

![image](https://github.com/user-attachments/assets/0133b65f-89b1-4f2a-a4e1-591adb1cfe61)

**Категории**

![image](https://github.com/user-attachments/assets/7294bd5f-bf64-4b52-b09c-84bd9f4777d7)

**Транзакции**

![image](https://github.com/user-attachments/assets/4594365f-5af9-45f0-8048-5badb8e32333)

**Цели**

![image](https://github.com/user-attachments/assets/ae536cea-766f-4d5a-90f5-239b44640625)

**Регулярные платежи**

![image](https://github.com/user-attachments/assets/02f1276e-9bb7-4d83-9b08-81d7bb648ed3)

**Смена пароля**

![image](https://github.com/user-attachments/assets/291a6454-6159-4c65-8cd8-7e50e85d1d11)

**Уведомление о добавлении платежа**

![image](https://github.com/user-attachments/assets/b6be46eb-9abe-411a-a49c-8d165653add1)







