package com.opedio.jboss.service.discus;

public interface  ManagementAgentMBean {

    // Configure getters and setters for the message attribute

    String getServiceName();
    String getStateString();
     
    //void setThreadNumber(int thread_number);
    int getThreadNumber();
    
    // life cycle callback
    void create() throws Exception;
    void destroy() throws Exception;
    void start() throws Exception;
    void stop();

}
