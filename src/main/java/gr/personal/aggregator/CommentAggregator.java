package gr.personal.aggregator;

import gr.personal.consumer.ConsumerUtil;
import gr.personal.consumer.RedditConsumer;
import gr.personal.consumer.model.Thing;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Nick Kanakis on 22/7/2017.
 */
@Service
public class CommentAggregator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentAggregator.class);

    @Autowired
    private RedditConsumer redditConsumer;
    private static String lastFullname = null;

    public void forwardAggregate(String subreddit) {
        JSONArray result;
        if (lastFullname == null)
            result = redditConsumer.fetchInitialComment(subreddit);
        else
            result = redditConsumer.fetchForward(lastFullname);

        String tmpLastFullname = AggregatorUtil.extractLastFullname(result);

        if (tmpLastFullname == null || tmpLastFullname == "")
            return;

        lastFullname = tmpLastFullname;
        enqueue(result);
    }

    //TODO: Actually enqueue the result
    private void enqueue(JSONArray result) {
        PrintWriter writer =null;
        try {
            writer = new PrintWriter("comments.txt", "UTF-8");
            for (String tmp : AggregatorUtil.extractFullnames(result)) {
                writer.println("COMMENT: CurrentTread: " + Thread.currentThread().getName() + ", ID: " + tmp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        finally {
            writer.close();
        }
    }

//TODO: fix reversed Aggregator if possible
//    public void reversedAggregate(String subreddit) {
//
//        JSONArray newModels;
//
//        if (lastEnqueuedId == "") {
//            newModels = redditConsumer.fetchInitialComment(subreddit);
//            lastEnqueuedId = AggregatorUtil.extractFirstId(newModels);
//            lastEnqueuedFullname = AggregatorUtil.extractFirstFullname(newModels);
//            enqueue(newModels);
//            return;
//        }
//
//        newModels = redditConsumer.fetchReversedComments(subreddit);
//        String newModelsLatestId = AggregatorUtil.extractLastId(newModels);
//
//        /**
//         * If there are no new comments the last processed id will be the same as the currently received last id.
//         * In that case we do not want to enqueue again the models.
//         */
//        long currentLatestIdDec = Long.parseLong(newModelsLatestId, 36);
//        long lastEnquedIdDec = Long.parseLong(lastEnqueuedId, 36);
//        if ( currentLatestIdDec <= lastEnquedIdDec) {
//            LOGGER.debug("No new models, currentLatestId:{}, lastEnqueueId", currentLatestIdDec, lastEnquedIdDec);
//            return;
//        }
//
//        /**
//         * Find the most recently processed model in the list of models, then slice the list
//         * [index+1, models.lenght] and enqueue the result. This is done to avoid already processed
//         * models being processed again.
//         */
//        for (int index = 0; index <= newModels.length() - 1; index++) {
//            JSONObject innerModel = newModels.getJSONObject(index);
//            String currentId = innerModel.getJSONObject("data").getString("id");
//
//            if (currentId == lastEnqueuedId) {
//                JSONArray unProcessedModels = AggregatorUtil.splitArray(newModels, index)[1];
//                lastEnqueuedId = AggregatorUtil.extractLastId(unProcessedModels);
//                lastEnqueuedFullname = AggregatorUtil.extractLastFullname(unProcessedModels);
//                enqueue(unProcessedModels);
//                break;
//            }
//        }
//
//        /**
//         * There is a possibility that the last processed Id is not in the list of models that was received.
//         * In that case there is a gap and we need to patch it with a backlog request.
//         */
//        String firstFullnameOfNewModels = AggregatorUtil.extractFirstFullname(newModels);
//        Thing start = new Thing(AggregatorUtil.increaseByOne(lastEnqueuedFullname));
//        Thing end = new Thing(AggregatorUtil.decreaseByOne(firstFullnameOfNewModels));
//        JSONArray backlogModels = redditConsumer.fetchByRange(start, end);
//        enqueue(ConsumerUtil.concatArray(backlogModels, newModels));
//    }
}
