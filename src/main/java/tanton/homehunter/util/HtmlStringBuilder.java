package tanton.homehunter.util;


import tanton.homehunter.domain.hibernate.Listing;

public class HtmlStringBuilder {

    private final StringBuilder sb;
//    private final GoogleApiClient googleApiClient;
    private int currentIndentLevel;

    public HtmlStringBuilder() {
        this.sb = new StringBuilder();
        this.currentIndentLevel = 0;
//        this.googleApiClient = new GoogleApiClient();
    }

    public HtmlStringBuilder append(final String append) {
        appendBasicIndent();
        this.sb.append(append);
        newLine();
        return this;
    }

    public HtmlStringBuilder appendOpenTag(final String tag) {
        appendBasicIndent();
        this.sb.append("<").append(tag).append(">");
        newLine();
        return this;
    }

    public HtmlStringBuilder appendCloseTag(final String tag) {
        appendBasicIndent();
        this.sb.append("</").append(tag).append(">");
        newLine();
        return this;
    }

    public void appendListing(final Listing listing, final Reason reason) {
        this.sb.append("<h2><a href=\"").append(listing.getDetailsUrl()).append("\">[").append(reason.name()).append("] { £").append(listing.getPrice()).append(" } ").append(listing.getPropertyType()).append(" ").append(listing.getNumBedrooms()).append(" bed(s)</a></h2>")
                .append("<p>").append(listing.getShortDescription()).append("</p>")
                .append("<img src=\"").append(listing.getImageUrl()).append("\" width=\"50%\" height=\"50%\">\n");

//                W1A 1AA       NBH
//                  W12 7TQ        BC
    }


    @Override
    public String toString() {
        return this.sb.toString();
    }

    public HtmlStringBuilder indent() {
        indent(1);
        return this;
    }

    public HtmlStringBuilder indent(int count) {
        this.currentIndentLevel += count;
//        for (int i = 0; i < this.currentIndentLevel; i++)
//            this.sb.append("\t");
        return this;
    }

    public void newLine() {
        this.sb.append("\n");
    }

    private void appendBasicIndent() {
        indent(currentIndentLevel);
    }

    public enum Reason {
        NEW,
        UPDATE
    }
}
