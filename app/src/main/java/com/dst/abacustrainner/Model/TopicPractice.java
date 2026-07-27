package com.dst.abacustrainner.Model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TopicPractice {

    private Object questions;


    public List<Question> getQuestions() {

        if (questions == null) {
            return null;
        }


        if (questions instanceof String) {

            Type type =
                    new TypeToken<List<Question>>() {}.getType();

            return new Gson().fromJson(
                    (String) questions,
                    type
            );
        }


        return (List<Question>) questions;
    }


    public void setQuestions(Object questions) {
        this.questions = questions;
    }

}
