package com.example.final_project_be.domain.pt.repository.querydsl;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.entity.QPtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.schedule.entity.QScheduleAlarm;
import com.example.final_project_be.domain.schedule.enums.AlarmTargetType;
import com.example.final_project_be.domain.schedule.enums.AlarmType;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PtScheduleRepositoryImpl implements PtScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PtSchedule> findSchedulesForDayBeforeAlarm(LocalDateTime start, LocalDateTime end, LocalDate today) {
        log.debug("Finding schedules for day before alarm between {} and {}", start, end);

        QPtSchedule ptSchedule = QPtSchedule.ptSchedule;
        QScheduleAlarm alarm = QScheduleAlarm.scheduleAlarm;

        Expression<LocalDate> startDateExpr =
                Expressions.dateTemplate(LocalDate.class, "function('date', {0})", ptSchedule.startTime);

        JPQLQuery<Long> subQuery = JPAExpressions
                .select(alarm.id)
                .from(alarm)
                .where(
                        alarm.alarmType.eq(AlarmType.PT_BEFORE),
                        alarm.targetType.eq(AlarmTargetType.MEMBER),
                        alarm.targetId.eq(ptSchedule.ptContract.member.id),
                        alarm.targetDate.eq(startDateExpr)
                );

        List<PtSchedule> results = queryFactory
                .selectFrom(ptSchedule)
                .join(ptSchedule.ptContract).fetchJoin()
                .join(ptSchedule.ptContract.member).fetchJoin()
                .join(ptSchedule.ptContract.trainer).fetchJoin()
                .where(
                        ptSchedule.status.eq(PtScheduleStatus.SCHEDULED),
                        ptSchedule.startTime.between(start, end),
                        subQuery.notExists()
                )
                .fetch();

        log.debug("Found {} schedules for day before alarm", results.size());
        return results;
    }

    @Override
    public List<PtSchedule> findCancelledSchedulesDayOfOrDayBefore(LocalDate today) {
        QPtSchedule ps = QPtSchedule.ptSchedule;
        QScheduleAlarm sa = QScheduleAlarm.scheduleAlarm;

        // 오늘 범위
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);

        // 내일 범위
        LocalDate tomorrow = today.plusDays(1);
        LocalDateTime tomorrowStart = tomorrow.atStartOfDay();
        LocalDateTime tomorrowEnd = tomorrow.atTime(23, 59, 59);

        // 스케줄의 시작 날짜(YYYY-MM-DD) 추출
        Expression<LocalDate> scheduleDateExpr =
                Expressions.dateTemplate(LocalDate.class, "function('date', {0})", ps.startTime);

        // 이미 Trainer에게 PT_CANCEL 알림 전송된 스케줄은 제외
        JPQLQuery<Long> alarmExistsSubQuery = JPAExpressions
                .select(sa.id)
                .from(sa)
                .where(
                        sa.alarmType.eq(AlarmType.PT_CANCEL),
                        sa.targetType.eq(AlarmTargetType.TRAINER),
                        sa.targetId.eq(ps.ptContract.trainer.id),
                        sa.targetDate.eq(
                                Expressions.dateTemplate(
                                        LocalDate.class,
                                        "DATE({0})",
                                        ps.startTime
                                )
                        )
                );

        return queryFactory
                .selectFrom(ps)
                .join(ps.ptContract).fetchJoin()
                .join(ps.ptContract.member).fetchJoin()
                .join(ps.ptContract.trainer).fetchJoin()
                .where(
                        ps.status.eq(PtScheduleStatus.CANCELLED),
                        // 당일 범위 or 내일 범위
                        ps.startTime.between(todayStart, todayEnd)
                                .or(ps.startTime.between(tomorrowStart, tomorrowEnd)),
                        alarmExistsSubQuery.notExists()
                )
                .fetch();
    }

    @Override
    public Map<Long, List<PtSchedule>> findSchedulesForTrainerSummary(LocalDateTime start, LocalDateTime end) {
        log.debug("Finding schedules for trainer summary between {} and {}", start, end);

        QPtSchedule ptSchedule = QPtSchedule.ptSchedule;

        List<PtSchedule> schedules = queryFactory
                .selectFrom(ptSchedule)
                .join(ptSchedule.ptContract).fetchJoin()
                .join(ptSchedule.ptContract.member).fetchJoin()
                .join(ptSchedule.ptContract.trainer).fetchJoin()
                .where(
                        ptSchedule.status.eq(PtScheduleStatus.SCHEDULED),
                        ptSchedule.startTime.between(start, end)
                )
                .orderBy(ptSchedule.startTime.asc())
                .fetch();

        // 트레이너 ID별로 그룹화
        Map<Long, List<PtSchedule>> trainerSchedulesMap = schedules.stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getPtContract().getTrainer().getId()
                ));

        log.debug("Found schedules for {} trainers", trainerSchedulesMap.size());
        return trainerSchedulesMap;
    }
}
