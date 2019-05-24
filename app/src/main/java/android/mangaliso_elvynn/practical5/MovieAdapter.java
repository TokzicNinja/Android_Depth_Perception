package android.mangaliso_elvynn.practical5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context c, ArrayList<Movie> movie)
    {
        super(c,0,movie);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);

        Movie currentMovie = getItem(position);

        TextView movieTitle = listItem.findViewById(R.id.titleTextView);
        assert currentMovie != null;
        movieTitle.setText("Title: "+currentMovie.getTitle());

        TextView movieIMDB = listItem.findViewById(R.id.imdbTextView);
        movieIMDB.setText("IMDB: "+currentMovie.getIMDB());

        TextView movieSynopsis = listItem.findViewById(R.id.synopsisTextView);
        movieSynopsis.setText("Synopsis: "+currentMovie.getSynopsis());

        TextView movieReleaseDate = listItem.findViewById(R.id.releasedTextView);
        movieReleaseDate.setText("Release Date: "+currentMovie.getDate());

        NetworkImageView imageView = listItem.findViewById(R.id.movieImageView);
        ImageLoader imageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();
        imageView.setImageUrl(currentMovie.getImage(), imageLoader);


        return listItem;
    }
}
