CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO categories (category_id, category_name, standard, user_id) VALUES
    (uuid_generate_v4(), 'Аптеки', true, NULL),
    (uuid_generate_v4(), 'Одежда', true, NULL),
    (uuid_generate_v4(), 'Рестораны', true, NULL),
    (uuid_generate_v4(), 'Супермаркеты', true, NULL),
    (uuid_generate_v4(), 'Услуги', true, NULL),
    (uuid_generate_v4(), 'Кредиты', true, NULL);