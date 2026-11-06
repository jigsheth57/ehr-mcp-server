package com.healthcare.demo.mcp;

import com.healthcare.demo.mcp.knowledge.PolicyTools;
import com.healthcare.demo.mcp.operations.CareTools;
import com.healthcare.demo.shared.*;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import java.time.Clock;

@Configuration
public class McpConfiguration {
    @Bean ToolCallbackProvider hospitalTools(EhrTools ehr,PolicyTools policies,CareTools care){
        // @Tool methods are registered explicitly; a service annotation alone does not publish them.
        return MethodToolCallbackProvider.builder().toolObjects(ehr,policies,care).build();
    }
    @Bean CapabilityTokens taskPermissions(@Value("${demo.token-secret}")String secret){return new CapabilityTokens(secret,Clock.systemUTC());}
    @Bean FilterRegistrationBean<CapabilityFilter> toolAuthorization(CapabilityTokens tokens){
        var registration=new FilterRegistrationBean<>(new CapabilityFilter(tokens,"mcp"));registration.addUrlPatterns("/*");return registration;
    }
}
