-- Datos de prueba usuario isabel (password: 123456)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO public.users (username, email, password_hash)
VALUES ('isabel', 'isabel@memobox.com', crypt('123456', gen_salt('bf')))
ON CONFLICT (username) DO NOTHING;

DO $$
DECLARE
    v_user   uuid;
    v_deck1  uuid;
    v_deck2  uuid;
    v_deck3  uuid;
BEGIN
    SELECT id INTO v_user FROM public.users WHERE username = 'isabel';

    -- Estadisticas iniciales
    INSERT INTO public.user_stats (user_id, current_streak, best_streak, last_study_date, studied_today, total_reviews, total_correct, total_wrong)
    VALUES (v_user, 5, 12, current_date - 1, 0, 87, 71, 16)
    ON CONFLICT (user_id) DO UPDATE SET current_streak = 5, best_streak = 12, last_study_date = current_date - 1;

    -- Mazos
    INSERT INTO public.decks (user_id, name, description)
    VALUES (v_user, 'Vocabulario inglés', 'Palabras nivel B2') RETURNING id INTO v_deck1;
    INSERT INTO public.decks (user_id, name, description)
    VALUES (v_user, 'Programación Java', 'POO, colecciones y streams') RETURNING id INTO v_deck2;
    INSERT INTO public.decks (user_id, name, description)
    VALUES (v_user, 'Historia de España', 'Examen selectividad') RETURNING id INTO v_deck3;

    -- Tarjetas Vocabulario
    INSERT INTO public.cards (deck_id, question, answer, example, tag, next_review_at, last_reviewed_at, repetitions, interval_days, ease_factor, status, times_correct, times_wrong, mastery_level)
    VALUES
        (v_deck1, '¿Qué significa "acknowledge"?', 'Reconocer, admitir.', 'She acknowledged her mistake.', 'verbos', now() + interval '15 days', now() - interval '2 days', 6, 15, 2.6, 'mastered', 6, 0, 5),
        (v_deck1, '¿Qué significa "nevertheless"?', 'Sin embargo.', 'It was raining; nevertheless, they went out.', 'conectores', now() + interval '21 days', now() - interval '4 days', 7, 21, 2.8, 'mastered', 7, 0, 5),
        (v_deck1, '¿Qué significa "resilient"?', 'Resistente.', 'Children are often very resilient.', 'adjetivos', now() + interval '6 days', now() - interval '1 day', 4, 6, 2.4, 'review', 4, 1, 4),
        (v_deck1, '¿Qué significa "albeit"?', 'Aunque.', 'It was a success, albeit a modest one.', 'conectores', now() + interval '1 day', now() - interval '1 day', 2, 1, 2.2, 'learning', 2, 1, 2),
        (v_deck1, '¿Qué significa "scrutinize"?', 'Escudriñar.', '', 'verbos', now(), NULL, 0, 0, 2.5, 'new', 0, 0, 0);

    -- Tarjetas Java
    INSERT INTO public.cards (deck_id, question, answer, example, tag, next_review_at, last_reviewed_at, repetitions, interval_days, ease_factor, status, times_correct, times_wrong, mastery_level)
    VALUES
        (v_deck2, '¿Qué es la herencia?', 'Subclase adquiere metodos de superclase con extends.', '', 'POO', now() + interval '20 days', now() - interval '3 days', 7, 20, 2.7, 'mastered', 7, 0, 5),
        (v_deck2, '¿ArrayList vs LinkedList?', 'ArrayList usa array dinamico, LinkedList nodos enlazados.', '', 'colecciones', now() + interval '8 days', now() - interval '1 day', 5, 8, 2.5, 'mastered', 5, 1, 5),
        (v_deck2, '¿Qué es un Stream?', 'Secuencia para operaciones funcionales.', '', 'streams', now() + interval '4 days', now() - interval '2 days', 4, 4, 2.4, 'review', 4, 1, 4),
        (v_deck2, '¿Qué hace Optional?', 'Evita NullPointerException.', '', 'util', now(), NULL, 0, 0, 2.5, 'new', 0, 0, 0);

    -- Tarjetas Historia
    INSERT INTO public.cards (deck_id, question, answer, example, tag, next_review_at, last_reviewed_at, repetitions, interval_days, ease_factor, status, times_correct, times_wrong, mastery_level)
    VALUES
        (v_deck3, '¿Año fin Guerra Civil?', '1939.', '', 'siglo XX', now() + interval '30 days', now() - interval '5 days', 8, 30, 2.9, 'mastered', 8, 0, 5),
        (v_deck3, '¿Constitución de Cádiz?', 'La Pepa (1812).', '', 'siglo XIX', now() + interval '14 days', now() - interval '2 days', 6, 14, 2.6, 'mastered', 6, 1, 5),
        (v_deck3, '¿Transición?', 'Paso de dictadura a democracia (1975-1978).', '', 'siglo XX', now() + interval '7 days', now() - interval '1 day', 4, 7, 2.4, 'review', 4, 0, 4);

    -- Sesiones
    INSERT INTO public.study_sessions (user_id, deck_id, session_date, reviewed_count, correct_count, wrong_count, duration_seconds)
    VALUES
        (v_user, v_deck1, current_date - 1, 10, 8, 2, 480),
        (v_user, v_deck2, current_date - 2, 8, 7, 1, 360),
        (v_user, v_deck3, current_date - 3, 12, 9, 3, 600),
        (v_user, NULL, current_date - 6, 20, 16, 4, 900);
END $$;
