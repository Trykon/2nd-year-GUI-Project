/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.project2.v1;

import static java.lang.Double.parseDouble;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import twitter4j.GeoLocation;
import twitter4j.IDs;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author kamil
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    Button search;
    @FXML
    Canvas graph;
    @FXML
    Label iffound;
    @FXML
    TextField radius;
    @FXML
    TextField latitude;
    @FXML
    TextField longitude;
    @FXML
    ListView listView;
    ConfigurationBuilder cb = new ConfigurationBuilder()
            .setOAuthConsumerKey("--------Please-----")
            .setOAuthConsumerSecret("-----put--------")
            .setOAuthAccessToken("--------your-keys--")
            .setOAuthAccessTokenSecret("--here-------");
    TwitterFactory tf = new TwitterFactory(cb.build());

    ArrayList<pair> userslocations;
    ArrayList<String> nodes;

    @FXML
    public void searchloaction() throws InterruptedException {
        int maxFollowerCount = 0;
        userslocations = new ArrayList<>();
        nodes = new ArrayList<>();

        GraphicsContext gc = graph.getGraphicsContext2D();
        gc.setFill(Color.GAINSBORO);
        gc.fillRect(0, 0, 308, 308);
        Twitter twitter;
        twitter = tf.getInstance();
        ArrayList<User> users = new ArrayList<>();
        try {
            Query query = new Query("");

            GeoLocation location;
            location = new GeoLocation(parseDouble(latitude.getText()), parseDouble(longitude.getText()));
            Query.Unit unit = Query.KILOMETERS;
            query.setGeoCode(location, parseDouble(radius.getText()), unit);
            QueryResult result;

            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();

                for (Status tweet : tweets) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                    boolean q = false;
                    if (userslocations != null && !userslocations.isEmpty()) {
                        for (int i = 0; i < userslocations.size(); i++) {
                            if (userslocations.get(i).getName().equals(tweet.getUser().getScreenName())) {
                                q = true;
                                break;
                            }
                        }
                    }

                    if (!q && tweet.getGeoLocation() != null) {
                        pair n;
                        String latString = "";
                        String lonString = "";
                        int la = 0;
                        int lo = 0;
                        String geoString = tweet.getGeoLocation().toString();
                        for (int i = 0; i < geoString.length(); i++) {
                            if (geoString.charAt(i) == '=') {
                                if (la == 0) {
                                    la = 1;
                                } else if (la == -1) {
                                    lo = 1;
                                }
                            } else if (geoString.charAt(i) == ',') {
                                la = -1;
                            } else if (geoString.charAt(i) == '}') {
                                lo = -1;
                            } else if (la == 1) {
                                latString = latString + geoString.charAt(i);
                            } else if (lo == 1) {
                                lonString = lonString + geoString.charAt(i);
                            }
                        }
                        User thisUser;
                        thisUser = tweet.getUser();
                        double lat = parseDouble(latString);
                        double lon = parseDouble(lonString);
                        System.out.println(tweet.getGeoLocation().toString());
                        n = new pair(tweet.getUser().getScreenName(), lat, lon);
                        userslocations.add(n);
                        users.add(thisUser);
                        if (thisUser.getFollowersCount() > maxFollowerCount) {
                            maxFollowerCount = thisUser.getFollowersCount();
                        }
                    }

                }
            } while ((query = result.nextQuery()) != null);
            for (int i = 0; i < users.size(); i++) {
                if (i % 14 == 0 && i != 0) {
                    Thread.sleep(1000 * 60 * 15 + 30);
                }
                IDs friends;
                friends = twitter.getFriendsIDs(users.get(i).getId(), -1);
                for (long j : friends.getIDs()) {
                    for (int k = i + 1; k < users.size(); k++) {
                        if (users.get(k).getId() == j) {
                            nodes.add(users.get(i).getScreenName() + ":" + users.get(k).getScreenName());
                        }
                    }
                }
            }

        } catch (TwitterException te) {
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
        double xmin;
        double xmax;
        double ymin;
        double ymax;
        xmin = userslocations.get(0).getA();
        xmax = userslocations.get(0).getA();
        ymin = userslocations.get(0).getB();
        ymax = userslocations.get(0).getB();
        for (int i = 1; i < userslocations.size(); i++) {
            if (xmin > userslocations.get(i).getA()) {
                xmin = userslocations.get(i).getA();
            }
            if (xmax < userslocations.get(i).getA()) {
                xmax = userslocations.get(i).getA();
            }
            if (ymin > userslocations.get(i).getB()) {
                ymin = userslocations.get(i).getB();
            }
            if (ymax < userslocations.get(i).getB()) {
                ymax = userslocations.get(i).getB();
            }
        }
        for (int i = 0; i < userslocations.size(); i++) {
            if (userslocations.get(i).getA() - xmin >= 0 && userslocations.get(i).getB() - ymin >= 0) {
                gc.setLineWidth(users.get(i).getFollowersCount() / maxFollowerCount * 3 + 1);
                gc.strokeOval((userslocations.get(i).getA() - xmin) / (xmax - xmin) * 300 + 4, (userslocations.get(i).getB() - ymin) / (ymax - ymin) * 300 + 4, 4, 4);
            }

        }
        ObservableList<String> usersLeftList = FXCollections.observableArrayList();
        for (int i = 0; i < users.size() - 1; i++) {
            User k = null;
            for (int j = i + 1; j < users.size(); j++) {
                if (users.get(j).getFollowersCount() > users.get(i).getFollowersCount()) {
                    k = users.get(i);
                    users.set(i, users.get(j));
                    users.set(j, k);
                }
            }
        }
        for (int i = 0; i < users.size() / 5; i++) {
            usersLeftList.add(users.get(i).getScreenName() + " " + users.get(i).getFollowersCount());
        }
        listView.setItems(usersLeftList);
        gc.setLineWidth(1);
        gc.setFill(Color.BLUE);
        for (int i = 0; i < nodes.size(); i++) {
            String user1 = "";
            String user2 = "";
            int p = 0;
            double x1 = 0;
            double x2 = 0;
            double y1 = 0;
            double y2 = 0;
            for (int j = 0; j < nodes.get(i).length(); j++) {
                if (nodes.get(i).charAt(j) == ':') {
                    p = 1;
                } else if (p == 0) {
                    user1 = user1 + nodes.get(i).charAt(j);
                } else if (p == 1) {
                    user2 = user2 + nodes.get(i).charAt(j);
                }
            }
            for (int j = 0; j < userslocations.size(); j++) {
                if (userslocations.get(j).getName().equals(user1)) {
                    x1 = (userslocations.get(j).getA() - xmin) / (xmax - xmin) * 300 + 6;
                    y1 = (userslocations.get(j).getB() - ymin) / (ymax - ymin) * 300 + 6;
                } else if (userslocations.get(j).getName().equals(user2)) {
                    x2 = (userslocations.get(j).getA() - xmin) / (xmax - xmin) * 300 + 6;
                    y2 = (userslocations.get(j).getB() - ymin) / (ymax - ymin) * 300 + 6;
                }
            }
            gc.strokeLine(x1, y1, x2, y2);
            gc.fillOval(x1 - 2, y1 - 2, 4, 4);
            gc.fillOval(x2 - 2, y2 - 2, 4, 4);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}

/**
 * check if list is sorted reduce it to be only 20% add function to color
 * connected users on graph at the end 
*
 */
