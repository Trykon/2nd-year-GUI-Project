/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.project.v3;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;

public class FXMLDocumentController implements Initializable {

    @FXML
    Button button;

    @FXML
    PieChart chart;

    @FXML
    TextField ch1;
    @FXML
    TextField ch2;
    @FXML
    TextField ch3;
    @FXML
    TextField ch4;
    @FXML
    TextField ch5;
    @FXML
    TextField ch6;
    @FXML
    TextField ch7;
    @FXML
    TextField ch8;
    @FXML
    TextField ch9;
    @FXML
    TextField ch10;

    @FXML
    Label lab1;
    @FXML
    Label lab2;
    @FXML
    Label lab3;
    @FXML
    Label lab4;
    @FXML
    Label lab5;
    @FXML
    Label lab6;
    @FXML
    Label lab7;
    @FXML
    Label lab8;
    @FXML
    Label lab9;
    @FXML
    Label lab10;
    @FXML
    Label label;

    @FXML
    Label authURL;

    @FXML
    TextField customkey;
    @FXML
    TextField customsecret;
    @FXML
    TextField appkey;
    @FXML
    TextField appsecret;
    @FXML
    TextField parent;
    @FXML
    TextField pinkey;
    @FXML
    Label move;

    Twitter twitter;

    ConfigurationBuilder cb = new ConfigurationBuilder()
            .setOAuthConsumerKey("--------Please-----")
            .setOAuthConsumerSecret("-----put--------")
            .setOAuthAccessToken("--------your-keys--")
            .setOAuthAccessTokenSecret("--here-------");
    TwitterFactory tf = new TwitterFactory(cb.build());

    @FXML
    public void btn(ActionEvent event) {
        twitter = tf.getInstance();
        String[] fields = {ch1.getText(), ch2.getText(), ch3.getText(), ch4.getText(), ch5.getText(), ch6.getText(), ch7.getText(), ch8.getText(), ch9.getText(), ch10.getText()};
        int[] amount = new int[10];
        for (int i = 0; i < 10; i++) {
            amount[i] = 0;
        }
        Query query = new Query(parent.getText());
        query.setLang("en");
        QueryResult result;
        int number = 0;
        try {
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                number += tweets.size();
                for (Status tweet : tweets) {
                    String tweetText = tweet.getText();
                    System.out.println(tweetText);
                    for (int i = 0; i < 10; i++) {
                        if ((tweetText.startsWith(fields[i] + " ") || (tweetText.endsWith(" " + fields[i]) || tweetText.contains(" " + fields[i] + " "))) && fields[i].length() > 0) {
                            amount[i]++;
                        }
                    }
                }
            } while ((query = result.nextQuery()) != null);
        } catch (TwitterException ex) {
        }

        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();

        for (int i = 0; i < 10; i++) {
            if (fields[i].length() > 0) {
                list.add(new PieChart.Data(fields[i], amount[i]));

            }
            switch (i) {
                case 0:
                    lab1.setText("" + amount[i]);
                    break;
                case 1:
                    lab2.setText("" + amount[i]);
                    break;
                case 2:
                    lab3.setText("" + amount[i]);
                    break;
                case 3:
                    lab4.setText("" + amount[i]);
                    break;
                case 4:
                    lab5.setText("" + amount[i]);
                    break;
                case 5:
                    lab6.setText("" + amount[i]);
                    break;
                case 6:
                    lab7.setText("" + amount[i]);
                    break;
                case 7:
                    lab8.setText("" + amount[i]);
                    break;
                case 8:
                    lab9.setText("" + amount[i]);
                    break;
                case 9:
                    lab10.setText("" + amount[i]);
                    break;
                default:
                    System.out.print(" ");
            }
        }
        chart.setData(list);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
