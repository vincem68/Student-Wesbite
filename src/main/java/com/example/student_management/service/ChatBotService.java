package com.example.student_management.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatBotService {

    @Value("${spring.ai.google.genai.api-key}")
    private String APIKey;

    @Value("${front.end.url}")
    private String frontendURL;

    private final ChatClient chatClient;

    @PersistenceContext
    private EntityManager entityManager;

    public ChatBotService(ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder.build();
    }

    public String askForStatistics(String question){

        //generate a query here by putting in the rules for our schema and other rules
        String prompt =
                """
                You are a MySQL generator who's job is to create SQL queries based on the schema provided and what
                questions a user may ask you.
                
                Here is the schema for the database:
                
                - Table: student (id INT, course VARCHAR, email VARCHAR, name VARCHAR, city VARCHAR, phone_number VARCHAR, address VARCHAR, date_of_birth VARCHAR,
                		departments VARCHAR, gender VARCHAR, parent_name VARCHAR, is_admin BOOLEAN)
                
                - Rules:
                    - You will output raw SQL statements ONLY.
                    - You will ONLY create SELECT queries. You will NOT create statements that alter the table or database in any way.
                    - If you cannot generate a SELECT statement based on the schema provided and the user's question, simply state "I don't know the answer to that question."
                    - The departments column in particular contains a VARCHAR of some combination of ece, it, cse, civil, and mech, all lowercase and separated by spaces. ece is electrical 
                      and computer engineering, it is Internet Technology, civil is civil and environmental engineering, cse is computer science engineering, and mech is mechanical
                      engineering. 
                    - The name, email, city, address, and parent_name columns all contain lowercase values only.
                		
                The user's question: """ + question;

        //send the prompt to Gemini to create a query
        String sqlStatement = chatClient.prompt()
                .user(prompt) //the user inputted message to the AI
                .call() //send the message to the AI platform
                .content(); //the response from the AI

        if (sqlStatement == null){
            return "I don't know the answer to that question.";
        }

        //make sure the generated sql query is a select statement
        String queryCheck = sqlStatement.substring(0, 6).toUpperCase();
        if (!queryCheck.equals("SELECT")){
            return "I cannot perform your requested action. Sorry!";
        }

        Query query = entityManager.createNativeQuery(sqlStatement);
        List<?> results = query.getResultList();

        String responsePrompt = "The user asked " + question + " and the result was " + results
                + ". Make a response based off these results.";

        return chatClient.prompt()
                .user(responsePrompt)
                .call()
                .content();
    }

    public String askForWebsiteInfo(String question){
        String prompt = """
                You are a website assistant that answers general questions about the university and the website, 
                which is hosted on """ + frontendURL + "." + """
                
                If the user asks for links to other pages on the website, we have the 
                /register page, then under the /directory page, we have pages for /email, 
                /announcements, /tuition, /degree, /clubs, /courses, /assignments, /events, 
                and /settings.
                
                If the user asks what each page is for, give a generic description of what each 
                page's purpose is and what you can do on that page.
                
                You will NOT generate any SQL statements that alter the table, or select any other information.
              
                The question from the user: 
                """ + question;

        //send the prompt to Gemini to create a query
        return chatClient.prompt()
                .user(prompt) //the user inputted message to the AI
                .call() //send the message to the AI platform
                .content(); //the response from the AI
    }

    /**
     * This function is used to ask general questions about the courses we offer
     * as well as giving advice on what courses to take based off user's preferences
     * @param question - user's text question
     * @return - a text answer
     */
    public String askCourseQuestions(String question){

        System.out.println("Inside Chatbot Service");
        String prompt = """
                You are a website assistant that answers general questions about the different courses we offer
                at a university.
                    
                The courses we offer are Cooking, Biology, Computer Science, Engineering, Nature, Expository Writing, 
                Philosophy, Chemistry, Public Speaking, History, Geography, Geology, and Robotics.
                
                If the user asks what course you would recommend they take, make a guess based off their interests.
                
                If the user asks what a course is about, respond with what a typical course of that subject
                would include, with a generic syllabus.
                
                If the user asks if the class is full, generate an SQL SELECT statement that gets the 
                count of users in the requested course. The table is called 'student' with column 'course'.
                The values of the course column will ONLY be the names of the courses we offer, which are Cooking, 
                Biology, Computer Science, Engineering, Nature, Expository Writing, Philosophy, Chemistry, Public Speaking, 
                History, Geography, Geology, and Robotics. The courses are offered every semester.
                If the count is at or over 50, say the course is full. Do NOT make up false information regarding how many 
                spots are open or if the course is actually closed or not.
                
                You will NOT generate any SQL statements that alter the table, or select any other information.
                
                If the user asks a question about a course we do not offer, just say we do not offer that course.
              
                The question from the user: 
                """ + question;

        //send the prompt to Gemini to create a query
        String answer = chatClient.prompt()
                .user(prompt) //the user inputted message to the AI
                .call() //send the message to the AI platform
                .content(); //the response from the AI

        if (answer != null && answer.contains("COUNT(*)")){
            String sql = answer.substring(answer.indexOf("SELECT"), answer.indexOf(";") + 1);
            System.out.println("Query is " + sql);
            Query query = entityManager.createNativeQuery(sql);
            List<?> results = query.getResultList();

            String sqlResponse = "The user asked " + question + " and the result was " + results
                    + ". Make a response based off these results.";
            return chatClient.prompt()
                    .user(sqlResponse)
                    .call()
                    .content();
        }

        return answer;
    }
}
