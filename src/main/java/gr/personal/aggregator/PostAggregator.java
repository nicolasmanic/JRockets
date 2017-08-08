package gr.personal.aggregator;

import gr.personal.consumer.RedditConsumer;
import gr.personal.utils.ModelsUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by Nick Kanakis on 22/7/2017.
 */
@Component("PostAggregator")
public class PostAggregator implements Aggregator {
    @Autowired
    private Logger logger;

    @Autowired
    private RedditConsumer redditConsumer;
    private static String lastFullname = null;

    @Override
    public void forwardAggregate(String subreddit) {
        JSONArray result;
        if (lastFullname == null)
            result = redditConsumer.fetchInitialPost(subreddit);
        else
            result = redditConsumer.fetchForward(lastFullname);

        String tmpLastFullname = ModelsUtils.extractLastFullname(result);

        if (tmpLastFullname == null || tmpLastFullname == "")
            return;

        lastFullname = tmpLastFullname;
        enqueue(result);
    }

    @Override
    public void reversedAggregate(String subreddit) {
        throw new RuntimeException("ReversedAggregate in not yet supported");
    }

    //TODO: Actually enqueue the result
    private void enqueue(JSONArray result) {
        File log = new File("posts.txt");
        PrintWriter out = null;
        try {
            if (out == null)
                out = new PrintWriter(new FileWriter(log, true));
            for (String id : ModelsUtils.extractIds(result)) {
                out.println(id);
                logger.debug("POST: CurrentTread: {}, ID: {}", Thread.currentThread().getName(), id);
            }
        } catch (IOException e) {

        } finally {
            out.close();
        }
    }
}
