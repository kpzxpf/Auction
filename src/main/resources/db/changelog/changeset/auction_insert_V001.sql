INSERT INTO users (username, email, password_hash, balance, role)
VALUES ('user1', 'user1@example.com', 'hashedpassword1', 500.00, 'user'),
       ('user2', 'user2@example.com', 'hashedpassword2', 1000.00, 'user'),
       ('admin', 'admin@example.com', 'hashedadminpassword', 0.00, 'admin');

INSERT INTO categories (name, description)
VALUES ('Антиквариат', 'Старинные и коллекционные предметы'),
       ('Искусство', 'Картины, скульптуры и другие предметы искусства'),
       ('Электроника', 'Гаджеты и бытовая техника'),
       ('Автомобили', 'Легковые и грузовые автомобили'),
       ('Недвижимость', 'Квартиры, дома и коммерческая недвижимость');

INSERT INTO lots (title, description, starting_price, current_price, seller_id, category_id, status, start_time, end_time)
VALUES
    -- Категория 1: Антиквариат
    ('Старинная монета', 'Редкая монета 19 века', 1000.00, 1000.00, 1, 1, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Антикварная книга', 'Книга 18 века в кожаном переплете', 5000.00, 5000.00, 2, 1, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Винтажные часы', 'Часы карманные, сделаны в Англии', 2500.00, 2500.00, 1, 1, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Фарфоровая статуэтка', 'Редкая статуэтка 19 века', 3200.00, 3200.00, 3, 1, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Старинный сервиз', 'Чайный сервиз начала 20 века', 4300.00, 4300.00, 2, 1, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Коллекционный жетон', 'Жетон из серебра, 1890 год', 2800.00, 2800.00, 3, 1, 'active', NOW(), NOW() + INTERVAL '7 days'),

    -- Категория 2: Искусство
    ('Картина Ван Гога', 'Репродукция картины Ван Гога', 2000.00, 2000.00, 2, 2, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Картина Пикассо', 'Репродукция картины Пикассо', 3500.00, 3500.00, 1, 2, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Авторская скульптура', 'Скульптура из бронзы', 8000.00, 8000.00, 3, 2, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Современный арт', 'Картина в стиле абстракционизм', 3000.00, 3000.00, 1, 2, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Редкая гравюра', 'Гравюра на дереве, 18 век', 7200.00, 7200.00, 2, 2, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Картина Моне', 'Репродукция картины Клода Моне', 4200.00, 4200.00, 3, 2, 'active', NOW(), NOW() + INTERVAL '7 days'),

    -- Категория 3: Электроника
    ('Ноутбук Apple', 'MacBook Pro 16 дюймов', 1500.00, 1500.00, 1, 3, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Смартфон Samsung', 'Galaxy S22 Ultra 256GB', 1000.00, 1000.00, 2, 3, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Игровая консоль', 'PlayStation 5 в идеальном состоянии', 750.00, 750.00, 3, 3, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Умные часы', 'Apple Watch Series 8', 400.00, 400.00, 1, 3, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Телевизор OLED', 'LG OLED 65 дюймов', 2300.00, 2300.00, 2, 3, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Ноутбук ASUS', 'ASUS ROG Strix для гейминга', 1800.00, 1800.00, 3, 3, 'active', NOW(), NOW() + INTERVAL '7 days'),

    -- Категория 4: Автомобили
    ('Автомобиль BMW', 'BMW X5 2021 года', 25000.00, 25000.00, 2, 4, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Автомобиль Tesla', 'Tesla Model S 2022 года', 60000.00, 60000.00, 1, 4, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Мотоцикл Ducati', 'Спортивный байк Ducati Panigale', 15000.00, 15000.00, 3, 4, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Грузовик Volvo', 'Грузовик Volvo FH 2020', 35000.00, 35000.00, 2, 4, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Ретро автомобиль', 'Ford Mustang 1967 года', 80000.00, 80000.00, 1, 4, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Авто Mercedes', 'Mercedes-Benz G-Class 2022', 120000.00, 120000.00, 3, 4, 'active', NOW(), NOW() + INTERVAL '7 days'),

    -- Категория 5: Недвижимость
    ('Квартира в центре', '3-комнатная квартира в центре города', 80000.00, 80000.00, 1, 5, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Дом у озера', 'Загородный дом на берегу озера', 150000.00, 150000.00, 2, 5, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Коммерческое помещение', 'Офисное здание в центре', 300000.00, 300000.00, 3, 5, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Пентхаус с видом', 'Элитный пентхаус в Москве', 500000.00, 500000.00, 1, 5, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Земельный участок', 'Участок земли 20 соток', 40000.00, 40000.00, 2, 5, 'active', NOW(), NOW() + INTERVAL '7 days'),
    ('Дом в горах', 'Шале в Альпах', 250000.00, 250000.00, 3, 5, 'active', NOW(), NOW() + INTERVAL '7 days');

INSERT INTO bids (lot_id, user_id, amount, created_at)
VALUES
    (1, 2, 1100.00, NOW()),
    (2, 1, 2100.00, NOW()),
    (3, 2, 1600.00, NOW()),
    (4, 1, 26000.00, NOW()),
    (5, 2, 81000.00, NOW());

INSERT INTO transactions (user_id, lot_id, amount, type, created_at)
VALUES
    (2, 1, 1100.00, 'payment', NOW()),
    (1, 2, 2100.00, 'payment', NOW()),
    (2, 3, 1600.00, 'payment', NOW()),
    (1, 4, 26000.00, 'payment', NOW()),
    (2, 5, 81000.00, 'payment', NOW());