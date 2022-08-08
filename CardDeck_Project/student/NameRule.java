package student;

import java.util.Objects;

public class NameRule {
    String winningName;
    String loosingName;

    public NameRule(String winningName, String loosingName) {
        this.winningName=winningName;
        this.loosingName=loosingName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameRule nameRule = (NameRule) o;
        return
                Objects.equals(winningName, nameRule.winningName) &&
                Objects.equals(loosingName, nameRule.loosingName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winningName, loosingName);
    }
}
