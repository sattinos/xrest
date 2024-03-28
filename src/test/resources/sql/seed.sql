-- Create authors
INSERT INTO Author (full_name, birth_date, created_at) VALUES
('J.K. Rowling', '1965-07-31', CURRENT_TIMESTAMP),
('Stephen King', '1947-09-21', CURRENT_TIMESTAMP),
('Haruki Murakami', '1949-01-12', CURRENT_TIMESTAMP),
('Margaret Atwood', '1939-11-18', CURRENT_TIMESTAMP),
('George R.R. Martin', '1948-09-20', CURRENT_TIMESTAMP);

-- Create books for each author
-- J.K. Rowling
INSERT INTO Book (title, publish_date, edition, volume, press, no_pages, created_at) VALUES
('Harry Potter and the Philosopher''s Stone', '1997-06-26', 1, 1, 'Bloomsbury Publishing', 223, CURRENT_TIMESTAMP),
('Harry Potter and the Chamber of Secrets', '1998-07-02', 1, 1, 'Bloomsbury Publishing', 251, CURRENT_TIMESTAMP);

-- Assign books to J.K. Rowling
INSERT INTO author_books (author_id, book_id) VALUES
(1, 1), -- Harry Potter and the Philosopher's Stone
(1, 2); -- Harry Potter and the Chamber of Secrets

-- Stephen King
INSERT INTO Book (title, publish_date, edition, volume, press, no_pages, created_at) VALUES
('The Shining', '1977-01-28', 1, 1, 'Doubleday', 447, CURRENT_TIMESTAMP),
('Misery', '1987-06-08', 1, 1, 'Viking Press', 310, CURRENT_TIMESTAMP);

-- Assign books to Stephen King
INSERT INTO author_books (author_id, book_id) VALUES
(2, 3), -- The Shining
(2, 4); -- Misery

-- Haruki Murakami
INSERT INTO Book (title, publish_date, edition, volume, press, no_pages, created_at) VALUES
('Norwegian Wood', '1987-09-04', 1, 1, 'Kodansha', 296, CURRENT_TIMESTAMP),
('1Q84', '2009-05-29', 1, 1, 'Shinchosha', 1157, CURRENT_TIMESTAMP);

-- Assign books to Haruki Murakami
INSERT INTO author_books (author_id, book_id) VALUES
(3, 5), -- Norwegian Wood
(3, 6); -- 1Q84

-- Margaret Atwood
INSERT INTO Book (title, publish_date, edition, volume, press, no_pages, created_at) VALUES
('The Handmaid''s Tale', '1985-09-24', 1, 1, 'McClelland & Stewart', 311, CURRENT_TIMESTAMP),
('Alias Grace', '1996-09-03', 1, 1, 'McClelland & Stewart', 560, CURRENT_TIMESTAMP);

-- Assign books to Margaret Atwood
INSERT INTO author_books (author_id, book_id) VALUES
(4, 7), -- The Handmaid's Tale
(4, 8); -- Alias Grace

-- George R.R. Martin
INSERT INTO Book (title, publish_date, edition, volume, press, no_pages, created_at) VALUES
('A Game of Thrones', '1996-08-06', 1, 1, 'Bantam Spectra', 694, CURRENT_TIMESTAMP),
('A Clash of Kings', '1998-11-16', 1, 1, 'Bantam Spectra', 768, CURRENT_TIMESTAMP);

-- Assign books to George R.R. Martin
INSERT INTO author_books (author_id, book_id) VALUES
(5, 9), -- A Game of Thrones
(5, 10); -- A Clash of Kings
