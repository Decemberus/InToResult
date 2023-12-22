package org.enjoy;

import org.enjoy.agent.AgentTransform;

import java.lang.instrument.Instrumentation;

public class PreMain{
    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new AgentTransform());
    }
}
