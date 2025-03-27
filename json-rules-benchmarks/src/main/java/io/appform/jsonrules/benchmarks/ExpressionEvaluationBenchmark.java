package io.appform.jsonrules.benchmarks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.jsonrules.Expression;
import io.appform.jsonrules.ExpressionEvaluationContext;
import io.appform.jsonrules.config.JsonRulesConfiguration;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;

@Threads(value = 5)
@Timeout(time = 5)
@Fork
@Warmup(iterations = 10, time = 5)
@Measurement(iterations = 3, time = 5)
@BenchmarkMode(value = Mode.Throughput)
public class ExpressionEvaluationBenchmark {
    @org.openjdk.jmh.annotations.State(value = Scope.Benchmark)
    public static class State {
        private Expression expression;
        private ExpressionEvaluationContext context;

        public State() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                expression = mapper.readValue(
                        ExpressionEvaluationBenchmark.class.getResourceAsStream("/expression.json"),
                        Expression.class);
                JsonNode jsonNode = mapper.readTree(
                        ExpressionEvaluationBenchmark.class.getResourceAsStream("/collection.json"));
                context = ExpressionEvaluationContext.builder()
                        .node(jsonNode)
                        .options(new HashMap<>())
                        .build();
                JsonRulesConfiguration.configure(JsonRulesConfiguration.PerformanceSafetyPreference.SPEED);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Benchmark
    public void evaluate(State state, Blackhole bh) {
        bh.consume(state.expression.evaluate(state.context));
    }
}
