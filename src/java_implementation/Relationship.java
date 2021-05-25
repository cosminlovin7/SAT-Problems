import java.util.Objects;

/**
 * Clasa folosita pentru a stoca relatiile dintre doua familii.
 */
public final class Relationship{
    private Integer family1;
    private Integer family2;

    public Relationship(final Integer family1,
                        final Integer family2) {
        this.family1 = family1;
        this.family2 = family2;
    }

    public Integer getFamily1() {
        return family1;
    }

    public Integer getFamily2() {
        return family2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relationship that = (Relationship) o;
        if (that.getFamily1() == this.family1
         && that.getFamily2() == this.family2) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(family1, family2);
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "family1=" + family1 +
                ", family2=" + family2 +
                '}';
    }
}