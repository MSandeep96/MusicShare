package com.lob.musicshare.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lob.musicshare.R;
import com.lob.musicshare.activity.OtherUserProfileActivity;
import com.lob.musicshare.json.ParseJson;
import com.lob.musicshare.query.Query;
import com.lob.musicshare.user.User;
import com.lob.musicshare.util.MaterialPalette;
import com.lob.musicshare.util.picasso.CircleTransform;
import com.lob.musicshare.util.ui.CircularRevealUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final boolean USE_CACHE = false;
    private final Activity activity;
    private ArrayList<User> users = new ArrayList<>();
    private String[] emails;
    private int materialColorIndex = new Random().nextInt(MaterialPalette.MATERIAL_COLORS.length);

    public UserAdapter(Activity activity, String[] emails) {
        this.emails = emails;
        this.activity = activity;
    }

    public UserAdapter(Activity activity, ArrayList<User> users) {
        this.users = users;
        this.activity = activity;
    }

    private User getUserAt(int index) {
        return users.get(index);
    }

    private void onClick(Intent intent, View profileImage) {
        Pair<View, String> pair = Pair.create(profileImage, "profileImage");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, pair);
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ViewHolder(inflater.inflate(R.layout.user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final View rootView = holder.rootView;
        final RelativeLayout cardBackground = holder.cardBackground;
        final ImageView profileImage = holder.profileImage;
        final TextView userName = holder.userName;
        final TextView moreInfo = holder.moreInfo;

        int backgroundColor;
        try {
            materialColorIndex++;
            int colorRes = MaterialPalette.MATERIAL_COLORS[materialColorIndex];
            backgroundColor = activity.getResources().getColor(colorRes);
        } catch (IndexOutOfBoundsException exception) {
            materialColorIndex = 0;
            int colorRes = MaterialPalette.MATERIAL_COLORS[materialColorIndex];
            backgroundColor = activity.getResources().getColor(colorRes);
        }

        cardBackground.setBackgroundColor(backgroundColor);

        CircularRevealUtils.enterReveal(rootView);

        boolean isFollowingFragment = users == null;
        if (isFollowingFragment) {
            final User user = getUserAt(position);

            final String profileImagePost = user.profileImageUrl;
            final String userNamePost = user.userName;
            final String artists = user.artists;
            final String genres = user.genres;
            final String email = user.email;

            if (!profileImagePost.equals("no")) {
                Picasso.with(rootView.getContext())
                        .load(profileImagePost + (!USE_CACHE ? "?time=" + System.currentTimeMillis() : ""))
                        .transform(new CircleTransform())
                        .fit()
                        .centerInside()
                        .into(profileImage);
            } else {
                Picasso.with(rootView.getContext())
                        .load(R.drawable.default_image_profile)
                        .transform(new CircleTransform())
                        .fit()
                        .centerInside()
                        .into(profileImage);
            }

            userName.setText(userNamePost);

            final String moreInfoText;
            if (!artists.equals("")) {
                moreInfoText = activity.getString(R.string.artists) + ": " + artists;
            } else if (!genres.equals("")) {
                moreInfoText = activity.getString(R.string.genres) + ": " + genres;
            } else {
                moreInfoText = activity.getString(R.string.no_more_info);
            }
            moreInfo.setText(moreInfoText);
            moreInfo.setText(moreInfoText);

            cardBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, OtherUserProfileActivity.class);
                    intent.putExtra("name-surname", userNamePost);
                    intent.putExtra("profile-image-url", profileImagePost);
                    intent.putExtra("email", email);
                    intent.putExtra("other-info", moreInfoText);
                    UserAdapter.this.onClick(intent, profileImage);
                }
            });
        } else {
            Query.getInstance(activity, Query.QueryType.GET_USER_INFO)
                    .setHisEmail(emails != null ? emails[position] : getUserAt(position).email)
                    .setShowDialog(false)
                    .setOnResultListener(new Query.OnResultListener() {
                        @Override
                        public void onResult(String result) {
                            if (!result.equals("no_one")) {
                                final ArrayList<User> arrayList = ParseJson.generateUsers(result);
                                User user = arrayList.get(0);

                                final String profileImagePost = user.profileImageUrl;
                                final String userNamePost = user.userName;
                                final String artists = user.artists;
                                final String genres = user.genres;

                                if (!profileImagePost.equals("no")) {
                                    Picasso.with(rootView.getContext())
                                            .load(profileImagePost)
                                            .transform(new CircleTransform())
                                            .fit()
                                            .centerInside()
                                            .into(profileImage);
                                } else {
                                    Picasso.with(rootView.getContext())
                                            .load(R.drawable.default_image_profile)
                                            .transform(new CircleTransform())
                                            .fit()
                                            .centerInside()
                                            .into(profileImage);
                                }

                                userName.setText(userNamePost);
                                userName.setTextColor(activity.getResources().getColor(R.color.md_white));

                                Spanned moreInfoSpanned;
                                String moreInfoString;
                                if (!artists.equals("")) {
                                    moreInfoString = "<b>" + activity.getString(R.string.artists) + "</b>" + ": " + artists;
                                } else if (!genres.equals("")) {
                                    moreInfoString = "<b>" + activity.getString(R.string.genres) + "</b>" + ": " + genres;
                                } else {
                                    moreInfoString = activity.getString(R.string.no_more_info);
                                }
                                moreInfoString = moreInfoString.replace(": , ", ": ");

                                moreInfoSpanned = Html.fromHtml(moreInfoString);
                                moreInfo.setText(moreInfoSpanned);
                                moreInfo.setTextColor(activity.getResources().getColor(R.color.md_white));

                                final String finalMoreInfoString = moreInfoString;
                                cardBackground.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(view.getContext(), OtherUserProfileActivity.class);
                                        intent.putExtra("name-surname", userNamePost);
                                        intent.putExtra("profile-image-url", profileImagePost);
                                        intent.putExtra("email", emails != null
                                                ? emails[position]
                                                : UserAdapter.this.getUserAt(position).email);
                                        intent.putExtra("other-info", finalMoreInfoString);
                                        UserAdapter.this.onClick(intent, profileImage);
                                    }
                                });
                            } else {
                                Toast.makeText(activity, R.string.no_one, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).startQuery();
        }
    }

    @Override
    public int getItemCount() {
        return emails != null ? emails.length : users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public RelativeLayout cardBackground;
        public ImageView profileImage;
        public TextView userName;
        public TextView moreInfo;

        public ViewHolder(View rootView) {
            super(rootView);

            this.rootView = rootView;
            this.profileImage = (ImageView) rootView.findViewById(R.id.profile_image);
            this.userName = (TextView) rootView.findViewById(R.id.text_view_user_name);
            this.moreInfo = (TextView) rootView.findViewById(R.id.text_view_more_info);
            this.cardBackground = (RelativeLayout) rootView.findViewById(R.id.card_background_color);
        }
    }
}