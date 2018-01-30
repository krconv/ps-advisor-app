package org.fundacionparaguaya.advisorapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A question targeting a background data which can be presented to a family and responded to from a
 * survey.
 */

public class BackgroundQuestion extends SurveyQuestion {
    @SerializedName("question_type")
    private QuestionType type;
    @SerializedName("options")
    private List<String> options;

    public BackgroundQuestion(String name,
                              String description,
                              ResponseType responseType,
                              QuestionType questionType) {
        this(name, description, responseType, questionType, new ArrayList<>());
    }
  
    public BackgroundQuestion(String name,
                              String description,
                              ResponseType responseType,
                              QuestionType questionType,
                              List<String> options) {
        super(name, description, responseType);
        this.type = questionType;
        this.options = options;
    }

    public QuestionType getQuestionType() {
        return type;
    }

    public List<String> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BackgroundQuestion that = (BackgroundQuestion) o;

        return options != null ? options.equals(that.options) : that.options == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (options != null ? options.hashCode() : 0);
        return result;
    }

    public enum QuestionType {
        PERSONAL,
        ECONOMIC
    }
}