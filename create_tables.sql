create extension if not exists pgcrypto;

create table public.users (
  id uuid primary key default gen_random_uuid(),
  username text not null unique,
  email text not null unique,
  password_hash text not null,
  created_at timestamptz default now()
);

create table public.decks (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.users(id) on delete cascade,
  name text not null,
  description text default '',
  created_at timestamptz default now()
);

create table public.cards (
  id uuid primary key default gen_random_uuid(),
  deck_id uuid not null references public.decks(id) on delete cascade,
  question text not null,
  answer text not null,
  example text default '',
  tag text default '',
  created_at timestamptz default now(),
  last_reviewed_at timestamptz,
  next_review_at timestamptz default now(),
  repetitions integer default 0,
  interval_days integer default 0,
  ease_factor double precision default 2.5,
  status text default 'new',
  times_correct integer default 0,
  times_wrong integer default 0,
  mastery_level integer default 0
);

create table public.study_sessions (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.users(id) on delete cascade,
  deck_id uuid references public.decks(id) on delete set null,
  session_date date default current_date,
  reviewed_count integer default 0,
  correct_count integer default 0,
  wrong_count integer default 0,
  duration_seconds integer default 0
);

create table public.user_stats (
  user_id uuid primary key references public.users(id) on delete cascade,
  current_streak integer default 0,
  best_streak integer default 0,
  last_study_date date,
  studied_today integer default 0,
  total_reviews integer default 0,
  total_correct integer default 0,
  total_wrong integer default 0
);