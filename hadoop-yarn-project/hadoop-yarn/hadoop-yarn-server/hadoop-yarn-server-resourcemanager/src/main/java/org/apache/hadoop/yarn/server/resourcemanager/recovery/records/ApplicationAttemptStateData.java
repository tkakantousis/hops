/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.recovery.records;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceAudience.Public;
import org.apache.hadoop.classification.InterfaceStability.Unstable;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos.ApplicationAttemptStateDataProto;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore.ApplicationAttemptState;
import org.apache.hadoop.yarn.util.Records;

/*
 * Contains the state data that needs to be persisted for an ApplicationAttempt
 */
@Public
@Unstable
public abstract class ApplicationAttemptStateData {
  public static ApplicationAttemptStateData newInstance(
      ApplicationAttemptId attemptId, Container container,
      ByteBuffer attemptTokens, long startTime, RMAppAttemptState finalState,
      String finalTrackingUrl, String diagnostics,
      FinalApplicationStatus amUnregisteredFinalStatus) {
    ApplicationAttemptStateData attemptStateData =
        Records.newRecord(ApplicationAttemptStateData.class);
    attemptStateData.setAttemptId(attemptId);
    attemptStateData.setMasterContainer(container);
    attemptStateData.setAppAttemptTokens(attemptTokens);
    attemptStateData.setState(finalState);
    attemptStateData.setFinalTrackingUrl(finalTrackingUrl);
    attemptStateData.setDiagnostics(diagnostics);
    attemptStateData.setStartTime(startTime);
    attemptStateData.setFinalApplicationStatus(amUnregisteredFinalStatus);
    return attemptStateData;
  }

  public static ApplicationAttemptStateData newInstance(
      ApplicationAttemptState attemptState) throws IOException {
    Credentials credentials = attemptState.getAppAttemptCredentials();
    ByteBuffer appAttemptTokens = null;
    if (credentials != null) {
      DataOutputBuffer dob = new DataOutputBuffer();
      credentials.writeTokenStorageToStream(dob);
      appAttemptTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
    }
    return newInstance(attemptState.getAttemptId(),
        attemptState.getMasterContainer(), appAttemptTokens,
        attemptState.getStartTime(), attemptState.getState(),
        attemptState.getFinalTrackingUrl(),
        attemptState.getDiagnostics(),
        attemptState.getFinalApplicationStatus());
  }

  public abstract ApplicationAttemptStateDataProto getProto();

  /**
   * The ApplicationAttemptId for the application attempt
   *
   * @return ApplicationAttemptId for the application attempt
   */
  @Public
  @Unstable
  public abstract ApplicationAttemptId getAttemptId();

  public abstract void setAttemptId(ApplicationAttemptId attemptId);

  /*
   * The master container running the application attempt
   * @return Container that hosts the attempt
   */
  @Public
  @Unstable
  public abstract Container getMasterContainer();

  public abstract void setMasterContainer(Container container);

  /**
   * The application attempt tokens that belong to this attempt
   *
   * @return The application attempt tokens that belong to this attempt
   */
  @Public
  @Unstable
  public abstract ByteBuffer getAppAttemptTokens();

  public abstract void setAppAttemptTokens(ByteBuffer attemptTokens);

  /**
   * Get the final state of the application attempt.
   *
   * @return the final state of the application attempt.
   */
  public abstract RMAppAttemptState getState();

  public abstract void setState(RMAppAttemptState state);

  /**
   * Get the original not-proxied <em>final tracking url</em> for the
   * application. This is intended to only be used by the proxy itself.
   *
   * @return the original not-proxied <em>final tracking url</em> for the
   * application
   */
  public abstract String getFinalTrackingUrl();

  /**
   * Set the final tracking Url of the AM.
   *
   * @param url
   */
  public abstract void setFinalTrackingUrl(String url);

  /**
   * Get the <em>diagnositic information</em> of the attempt
   *
   * @return <em>diagnositic information</em> of the attempt
   */
  public abstract String getDiagnostics();

  public abstract void setDiagnostics(String diagnostics);

  /**
   * Get the <em>start time</em> of the application.
   *
   * @return <em>start time</em> of the application
   */
  public abstract long getStartTime();

  public abstract void setStartTime(long startTime);

  /**
   * Get the <em>final finish status</em> of the application.
   *
   * @return <em>final finish status</em> of the application
   */
  public abstract FinalApplicationStatus getFinalApplicationStatus();

  public abstract void setFinalApplicationStatus(FinalApplicationStatus finishState);
  
  public abstract int getAMContainerExitStatus();

  public abstract void setAMContainerExitStatus(int exitStatus);
  /**
   * Get the <em>Progress</em> of the application.
   *
   * @return <em>Progress</em> of the application
   */
  public abstract float getProgress();

  public abstract void setProgress(float startTime);

  public abstract String getHost();

  public abstract void setHost(String Host);

  public abstract int getRpcPort();

  public abstract void setRpcPort(int rpcPort);

  public abstract void addALLRanNodes(List<YarnProtos.NodeIdProto> ranNodes);

  public abstract Set<NodeId> getRanNodes();

  public abstract List<ContainerStatus> getJustFinishedContainers();

  public abstract void addAllJustFinishedContainers(
      List<YarnProtos.ContainerStatusProto> justFinishedContainers);
}
