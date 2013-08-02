package ossindex.model;

import java.util.ArrayList;
import java.util.List;

public class ImageInfos {
	private List<ImageInfo> results = new ArrayList<ImageInfo>();

    public List<ImageInfo> getResults() {
        return results;
    }

    public void setResults(List<ImageInfo> results) {
        this.results = results;
    }
}
