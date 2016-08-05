package com.deere.isg.tx.excel.beans;

import java.time.LocalDateTime;

/**
 * Created by ganesh.vallabhaneni on 5/21/2016.
 */
public class TouchReportBean {

    private Integer casdId;
    private String supportGroup;
    private LocalDateTime assignmentDate;
    private String activity;
    private LocalDateTime activityDate;
    private Long duration;
    private String priority;
    private String application;
    private String agent;
    private String status;
    private LocalDateTime openDate;
    private LocalDateTime resolvedDate;


    public Integer getCasdId() {
        return casdId;
    }

    public void setCasdId(Integer casdId) {
        this.casdId = casdId;
    }

    public LocalDateTime getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public String getSupportGroup() {
        return supportGroup;
    }

    public void setSupportGroup(String supportGroup) {
        this.supportGroup = supportGroup;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public LocalDateTime getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDateTime activityDate) {
        this.activityDate = activityDate;
    }

    public Long getDuration() {
        return duration;
    }

    public Double getDurationInDays() {
        return duration / 86400.0;
    }


    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDateTime openDate) {
        this.openDate = openDate;
    }

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    @Override
    public String toString() {
        return "TouchReportBean{" +
                "casdId=" + casdId +
                ", supportGroup='" + supportGroup + '\'' +
                ", assignmentDate=" + assignmentDate +
                ", activity='" + activity + '\'' +
                ", activityDate=" + activityDate +
                ", duration=" + duration +
                ", priority='" + priority + '\'' +
                ", application='" + application + '\'' +
                ", agent='" + agent + '\'' +
                ", status='" + status + '\'' +
                ", openDate=" + openDate +
                ", resolvedDate=" + resolvedDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TouchReportBean that = (TouchReportBean) o;

        if (!casdId.equals(that.casdId)) return false;
        if (!supportGroup.equals(that.supportGroup)) return false;
        if (!assignmentDate.equals(that.assignmentDate)) return false;
        if (!activity.equals(that.activity)) return false;
        if (!activityDate.equals(that.activityDate)) return false;
        if (!duration.equals(that.duration)) return false;
        if (!priority.equals(that.priority)) return false;
        if (!application.equals(that.application)) return false;
        if (!agent.equals(that.agent)) return false;
        if (!status.equals(that.status)) return false;
        if (!openDate.equals(that.openDate)) return false;
        return resolvedDate.equals(that.resolvedDate);

    }

    @Override
    public int hashCode() {
        int result = casdId.hashCode();
        result = 31 * result + supportGroup.hashCode();
        result = 31 * result + assignmentDate.hashCode();
        result = 31 * result + activity.hashCode();
        result = 31 * result + activityDate.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + priority.hashCode();
        result = 31 * result + application.hashCode();
        result = 31 * result + agent.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + openDate.hashCode();
        result = 31 * result + resolvedDate.hashCode();
        return result;
    }
}
