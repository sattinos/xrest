-- Insert authors
INSERT INTO Author (full_name, birth_date, created_at, is_deleted) VALUES
  ('J.K. Rowling', '1965-07-31', CURRENT_DATE, 0),
  ('Stephen King', '1947-09-21', CURRENT_DATE, 0),
  ('Agatha Christie', '1890-09-15', CURRENT_DATE, 0),
  ('George R.R. Martin', '1948-09-20', CURRENT_DATE, 0),
  ('Tolkien', '1892-01-03', CURRENT_DATE, 0);

-- Insert books for each author with created_at and is_deleted set to 0
INSERT INTO Book (title, publish_date, edition, volume, press, no_pages, author_id, created_at, is_deleted) VALUES
  -- Books for J.K. Rowling
  ('Harry Potter and the Philosopher''s Stone', '1997-06-26', 1, 1, 'Bloomsbury Publishing', 223, 1, CURRENT_DATE, 0),
  ('Harry Potter and the Chamber of Secrets', '1998-07-02', 1, 2, 'Bloomsbury Publishing', 251, 1, CURRENT_DATE, 0),
  ('Harry Potter and the Prisoner of Azkaban', '1999-07-08', 1, 3, 'Bloomsbury Publishing', 317, 1, CURRENT_DATE, 0),
  ('Harry Potter and the Goblet of Fire', '2000-07-08', 1, 4, 'Bloomsbury Publishing', 636, 1, CURRENT_DATE, 0),
  ('Harry Potter and the Order of the Phoenix', '2003-06-21', 1, 5, 'Bloomsbury Publishing', 766, 1, CURRENT_DATE, 0),

  -- Books for Stephen King
  ('Carrie', '1974-04-05', 1, 1, 'Doubleday', 199, 2, CURRENT_DATE, 0),
  ('The Shining', '1977-01-28', 1, 1, 'Doubleday', 447, 2, CURRENT_DATE, 0),
  ('It', '1986-09-15', 1, 1, 'Viking Press', 1138, 2, CURRENT_DATE, 0),
  ('The Stand', '1978-10-03', 1, 1, 'Doubleday', 823, 2, CURRENT_DATE, 0),
  ('Misery', '1987-06-08', 1, 1, 'Viking Press', 310, 2, CURRENT_DATE, 0),

  -- Books for Agatha Christie
  ('Murder on the Orient Express', '1934-01-01', 1, 1, 'Collins Crime Club', 322, 3, CURRENT_DATE, 0),
  ('The Murder of Roger Ackroyd', '1926-06-19', 1, 1, 'William Collins, Sons', 312, 3, CURRENT_DATE, 0),
  ('And Then There Were None', '1939-11-06', 1, 1, 'Collins Crime Club', 247, 3, CURRENT_DATE, 0),
  ('Death on the Nile', '1937-11-01', 1, 1, 'Collins Crime Club', 288, 3, CURRENT_DATE, 0),
  ('The ABC Murders', '1936-01-06', 1, 1, 'Collins Crime Club', 256, 3, CURRENT_DATE, 0),

  -- Books for George R.R. Martin
  ('A Game of Thrones', '1996-08-06', 1, 1, 'Bantam Spectra', 694, 4, CURRENT_DATE, 0),
  ('A Clash of Kings', '1998-11-16', 1, 2, 'Bantam Spectra', 768, 4, CURRENT_DATE, 0),
  ('A Storm of Swords', '2000-10-31', 1, 3, 'Bantam Spectra', 973, 4, CURRENT_DATE, 0),
  ('A Feast for Crows', '2005-11-08', 1, 4, 'Bantam Spectra', 753, 4, CURRENT_DATE, 0),
  ('A Dance with Dragons', '2011-07-12', 1, 5, 'Bantam Spectra', 1040, 4, CURRENT_DATE, 0),

  -- Books for Tolkien
  ('The Hobbit', '1937-09-21', 1, 1, 'Allen & Unwin', 310, 5, CURRENT_DATE, 0),
  ('The Lord of the Rings', '1954-07-29', 1, 1, 'Allen & Unwin', 1178, 5, CURRENT_DATE, 0),
  ('The Silmarillion', '1977-09-15', 1, 1, 'Allen & Unwin', 365, 5, CURRENT_DATE, 0),
  ('The Children of Húrin', '2007-04-17', 1, 1, 'Houghton Mifflin', 313, 5, CURRENT_DATE, 0),
  ('The History of Middle-earth', '1983-07-01', 1, 1, 'Allen & Unwin', 4721, 5, CURRENT_DATE, 0),

  ('The Big Explosion', '1977-09-15', 1, 1, 'Allen & Unwin', 365, null, CURRENT_DATE, 0),
  ('The Return of Dragons', '2007-04-17', 1, 1, 'Houghton Mifflin', 313, null, CURRENT_DATE, 0),
  ('The Lost Village', '1983-07-01', 1, 1, 'Allen & Unwin', 4721, null, CURRENT_DATE, 0);