package gr.personal.aggregator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Nick Kanakis on 23/7/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostAggregatorTest {
    @Autowired
    private PostAggregator postAggregator;

    @Test
    public void testAggregate() throws Exception {
        postAggregator.forwardAggregate("all");
    }
}