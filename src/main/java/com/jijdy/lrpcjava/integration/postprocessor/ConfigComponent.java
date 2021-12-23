package com.jijdy.lrpcjava.integration.postprocessor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lrpc")
public class ConfigComponent {

    private  String registryAddr;

    private  String namespace;

    private  Integer zkSleepTimeout;

    private  Integer maxRetries;

    private  Integer port;

    private  String serializer;

    private  String loadBalance;

}
