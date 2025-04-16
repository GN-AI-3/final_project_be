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

}
