package org.example.memobox.service;

import org.example.memobox.model.UserStats;
import org.example.memobox.repository.StatsRepository;

import java.time.LocalDate;

public class StatsService {

    public UserStats getStats() throws Exception {
        return StatsRepository.getStats();
    }

    public UserStats recordStudySession(int reviewed, int correct, int wrong) throws Exception {
        UserStats stats = StatsRepository.getStats();
        LocalDate today = LocalDate.now();
        LocalDate lastStudy = stats.getLastStudyDate();

        int currentStreak = stats.getCurrentStreak();

        if (lastStudy == null) {
            currentStreak = 1;                       // primera vez
        } else if (lastStudy.equals(today)) {
            // Ya estudió hoy: la racha no cambia (sesión adicional del mismo día)
        } else if (lastStudy.equals(today.minusDays(1))) {
            currentStreak++;                         // estudió ayer: continúa la racha
        } else {
            currentStreak = 1;                       // más de un día sin estudiar: reinicia
        }

        int bestStreak = Math.max(stats.getBestStreak(), currentStreak);

        int studiedToday = (lastStudy != null && lastStudy.equals(today))
                ? stats.getStudiedToday() + reviewed
                : reviewed;

        stats.setCurrentStreak(currentStreak);
        stats.setBestStreak(bestStreak);
        stats.setLastStudyDate(today);
        stats.setStudiedToday(studiedToday);
        stats.setTotalReviews(stats.getTotalReviews() + reviewed);
        stats.setTotalCorrect(stats.getTotalCorrect() + correct);
        stats.setTotalWrong(  stats.getTotalWrong()   + wrong);

        StatsRepository.actualizarStats(stats);
        return stats;
    }
}
