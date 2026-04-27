SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM order_items;
DELETE FROM orders;
ALTER TABLE order_items AUTO_INCREMENT = 1;
ALTER TABLE orders AUTO_INCREMENT = 1;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO orders (
    id, user_id, order_number, period_start, period_end, cover_style,
    owner_name, status, snapshot_json, created_at, updated_at
)
VALUES
    (
        1, 1, 'MJ-20260424-0001', '2026-01-01', '2026-03-31', 'CLASSIC',
        '김독서', 'COMPLETED',
        JSON_OBJECT(
            'periodStart', '2026-01-01',
            'periodEnd', '2026-03-31',
            'coverStyle', 'CLASSIC',
            'ownerName', '김독서',
            'bookCount', 3,
            'quoteCount', 9,
            'books', JSON_ARRAY(
                JSON_OBJECT('bookId', 1, 'title', '작별하지 않는다', 'author', '한강', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 1)),
                JSON_OBJECT('bookId', 2, 'title', '도둑맞은 집중력', 'author', '요한 하리', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 2)),
                JSON_OBJECT('bookId', 3, 'title', '프로그래머의 길, 멘토에게 묻다', 'author', '데이브 후버, 애디웨일 오시나이', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 3))
            )
        ),
        '2026-04-01 09:00:00', '2026-04-02 18:00:00'
    ),
    (
        2, 1, 'MJ-20260424-0002', '2026-04-01', '2026-04-30', 'MINIMAL',
        '김독서', 'PENDING',
        JSON_OBJECT(
            'periodStart', '2026-04-01',
            'periodEnd', '2026-04-30',
            'coverStyle', 'MINIMAL',
            'ownerName', '김독서',
            'bookCount', 3,
            'quoteCount', 7,
            'books', JSON_ARRAY(
                JSON_OBJECT('bookId', 4, 'title', '물고기는 존재하지 않는다', 'author', '룰루 밀러', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 4)),
                JSON_OBJECT('bookId', 5, 'title', '아주 작은 습관의 힘', 'author', '제임스 클리어', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 5)),
                JSON_OBJECT('bookId', 6, 'title', '시를 잊은 그대에게', 'author', '정재찬', 'quoteCount', 1, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 6 AND q.created_at BETWEEN '2026-04-01 00:00:00' AND '2026-04-30 23:59:59'))
            )
        ),
        '2026-04-24 09:30:00', '2026-04-24 09:30:00'
    ),
    (
        3, 1, 'MJ-20260424-0003', '2026-04-01', '2026-04-30', 'MODERN',
        '김독서', 'PROCESSING',
        JSON_OBJECT(
            'periodStart', '2026-04-01',
            'periodEnd', '2026-04-30',
            'coverStyle', 'MODERN',
            'ownerName', '김독서',
            'bookCount', 3,
            'quoteCount', 7,
            'books', JSON_ARRAY(
                JSON_OBJECT('bookId', 4, 'title', '물고기는 존재하지 않는다', 'author', '룰루 밀러', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 4)),
                JSON_OBJECT('bookId', 5, 'title', '아주 작은 습관의 힘', 'author', '제임스 클리어', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 5)),
                JSON_OBJECT('bookId', 6, 'title', '시를 잊은 그대에게', 'author', '정재찬', 'quoteCount', 1, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 6 AND q.created_at BETWEEN '2026-04-01 00:00:00' AND '2026-04-30 23:59:59'))
            )
        ),
        '2026-04-24 10:00:00', '2026-04-24 10:00:00'
    ),
    (
        4, 1, 'MJ-20260424-0004', '2026-01-01', '2026-03-31', 'MINIMAL',
        '김독서', 'CANCELLED',
        JSON_OBJECT(
            'periodStart', '2026-01-01',
            'periodEnd', '2026-03-31',
            'coverStyle', 'MINIMAL',
            'ownerName', '김독서',
            'bookCount', 3,
            'quoteCount', 9,
            'books', JSON_ARRAY(
                JSON_OBJECT('bookId', 1, 'title', '작별하지 않는다', 'author', '한강', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 1)),
                JSON_OBJECT('bookId', 2, 'title', '도둑맞은 집중력', 'author', '요한 하리', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 2)),
                JSON_OBJECT('bookId', 3, 'title', '프로그래머의 길, 멘토에게 묻다', 'author', '데이브 후버, 애디웨일 오시나이', 'quoteCount', 3, 'quotes', (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', q.id, 'page', q.page, 'content', q.content, 'memo', q.memo, 'tags', COALESCE((SELECT JSON_ARRAYAGG(t.name) FROM quote_card_tags qct JOIN tags t ON t.id = qct.tag_id WHERE qct.quote_card_id = q.id), JSON_ARRAY()), 'createdAt', DATE_FORMAT(q.created_at, '%Y-%m-%dT%H:%i:%s'))) FROM quote_cards q WHERE q.book_id = 3))
            )
        ),
        '2026-04-24 10:30:00', '2026-04-24 10:30:00'
    );

INSERT INTO order_items (id, order_id, book_id, quote_count, display_order)
VALUES
    (1, 1, 1, 3, 1),
    (2, 1, 2, 3, 2),
    (3, 1, 3, 3, 3),
    (4, 2, 4, 3, 1),
    (5, 2, 5, 3, 2),
    (6, 2, 6, 1, 3),
    (7, 3, 4, 3, 1),
    (8, 3, 5, 3, 2),
    (9, 3, 6, 1, 3),
    (10, 4, 1, 3, 1),
    (11, 4, 2, 3, 2),
    (12, 4, 3, 3, 3);

ALTER TABLE orders AUTO_INCREMENT = 5;
ALTER TABLE order_items AUTO_INCREMENT = 13;
