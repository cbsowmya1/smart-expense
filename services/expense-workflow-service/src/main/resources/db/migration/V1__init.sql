create table if not exists users (
  id bigserial primary key,
  email varchar(255) unique not null,
  password_hash varchar(255) not null,
  role varchar(32) not null check (role in ('EMPLOYEE','MANAGER','FINANCE')),
  created_at timestamptz not null default now()
);

create table if not exists expenses (
  id bigserial primary key,
  user_id bigint not null references users(id) on delete cascade,
  amount numeric(12,2) not null,
  currency varchar(8) not null default 'INR',
  category varchar(64),
  description text,
  receipt_url text,
  decision varchar(16) not null default 'PENDING' check (decision in ('PENDING','APPROVE','REVIEW','REJECT')),
  violations jsonb not null default '[]'::jsonb,
  created_at timestamptz not null default now()
);