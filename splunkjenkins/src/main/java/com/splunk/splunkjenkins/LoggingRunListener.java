package com.splunk.splunkjenkins;


import com.google.common.collect.ImmutableMap;
import com.splunk.splunkjenkins.utils.SplunkLogService;
import hudson.model.*;
import hudson.model.listeners.RunListener;

import javax.annotation.Nonnull;

import hudson.Extension;

import java.util.Map;

import static com.splunk.splunkjenkins.Constants.JOB_RESULT;
import static com.splunk.splunkjenkins.utils.EventType.BUILD_EVENT;

@SuppressWarnings("unused")
@Extension
public class LoggingRunListener extends RunListener<Run> {
    UserActionDSL postJobAction = new UserActionDSL();

    @Override
    public void onStarted(Run run, TaskListener listener) {

        Map event = ImmutableMap.builder()
                .put(Constants.TAG, "job_event")
                .put(Constants.BUILD_ID, run.getUrl())
                .put("trigger_by", getBuildCause(run))
                .put("build_event", "started").build();
        SplunkLogService.getInstance().send(event, BUILD_EVENT);
    }

    private String getBuildCause(Run run) {
        StringBuilder buf = new StringBuilder(100);
        for (CauseAction action : run.getActions(CauseAction.class)) {
            for (Cause cause : action.getCauses()) {
                if(cause instanceof Cause.UpstreamCause){
                    //find nearest upstream
                    Cause.UpstreamCause upstreamCause=(Cause.UpstreamCause) cause;
                    //upstream url is project url, build is build number
                    return upstreamCause.getUpstreamUrl()+"/"+upstreamCause.getUpstreamBuild()+"/";
                }
                //manual triggered
                if (buf.length() > 0) buf.append(", ");
                buf.append(cause.getShortDescription());
            }
        }
        if (buf.length() == 0) {
            buf.append("system");
        }
        return buf.toString();
    }

    @Override
    public void onCompleted(Run run, @Nonnull TaskListener listener) {
        if (!(run instanceof AbstractBuild)) {
            return;
        }
        AbstractBuild build = (AbstractBuild) run;
        String jenkinsNode = (build.getBuiltOn() == null ? "unknown" : build.getBuiltOn().getDisplayName());
        //other messages no need to escape quote
        Map event = ImmutableMap.builder()
                .put(Constants.TAG, "job_event")
                .put(Constants.BUILD_ID, run.getUrl())
                .put("trigger_by", getBuildCause(run))
                .put(JOB_RESULT, build.getResult().toString())
                .put("job_started_at", build.getTimestampString2())
                .put("job_duration", build.getDuration() + "ms")
                .put("node", jenkinsNode)
                .build();
        SplunkLogService.getInstance().send(event, BUILD_EVENT);
        postJobAction.perform(build, listener);
    }
}