package nz.co.redice.azansilenttime.services.alarm_service;

public class Schedule implements Comparable<Schedule> {
    public final long epochSecond;
    public final boolean isActive;

    public Schedule(Long epochSecond, Boolean isActive) {
        this.epochSecond = epochSecond;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        if (epochSecond != schedule.epochSecond) return false;
        return isActive == schedule.isActive;
    }

    @Override
    public int hashCode() {
        int result = (int) (epochSecond ^ (epochSecond >>> 32));
        result = 31 * result + (isActive ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(Schedule schedule) {
        return Long.compare(this.epochSecond, schedule.epochSecond);
    }
}
