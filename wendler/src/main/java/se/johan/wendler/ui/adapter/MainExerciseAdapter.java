package se.johan.wendler.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.johan.wendler.R;
import se.johan.wendler.model.Action;
import se.johan.wendler.model.ExerciseSet;
import se.johan.wendler.model.MainExercise;
import se.johan.wendler.model.SetType;
import se.johan.wendler.ui.view.TextDrawable;
import se.johan.wendler.util.ColorGenerator;
import se.johan.wendler.util.PreferenceUtil;

/**
 * Adapter used in the list for displaying the main exercise during a workout.
 */
public class MainExerciseAdapter extends BaseAdapter {
    private final MainExercise mMainExercise;
    private final int mWeek;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final Action.ActionListener mActionListener;

    /**
     * Constructor for the adapter.
     */
    public MainExerciseAdapter(
            Context context,
            MainExercise exercise,
            int week,
            Action.ActionListener actionListener) {
        mMainExercise = exercise;
        mWeek = week;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mActionListener = actionListener;
    }

    /**
     * Return the number of sets to perform.
     */
    @Override
    public int getCount() {
        return mMainExercise.getSetGroups().size();
    }

    /**
     * Return the item at a certain position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Return the item id of a certain position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Return false to make the list items not clickable.
     */
    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * Return the view for a given position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        List<ExerciseSet> sets = getSetsByIndex(position);
        SetType setType = getTypeByIndex(position);
        boolean shouldShowRepsToBeat = shouldShowRepsToBeat(setType, mMainExercise);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.card_main_exercise, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.setOne = (TextView) convertView.findViewById(R.id.set_one);
            holder.setTwo = (TextView) convertView.findViewById(R.id.set_two);
            holder.setThree = (TextView) convertView.findViewById(R.id.set_three);
            holder.repsToBeat = (TextView) convertView.findViewById(R.id.reps_to_beat);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            String text = getSetTypeString(setType, true);
            int color = ColorGenerator.DEFAULT.getColor(text);
            holder.textDrawable = TextDrawable.builder().buildRound(text, color);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageDrawable(holder.textDrawable);

        // TODO MAKE sets dynamic

        final ExerciseSet setOne = sets.get(0);
        final ExerciseSet setTwo = sets.get(1);
        final ExerciseSet setThree = sets.get(2);
        final int repsToBeat = getRepsToBeat();

        String plusSet = setThree.getType().equals(SetType.PLUS_SET) && mWeek != 4
                ? "+" : "";

        holder.setOne.setText(
                String.format(mContext.getString(R.string.exercise_set_one),
                        String.valueOf(setOne.getWeight()),
                        String.valueOf(setOne.getRepGoal())));

        holder.setTwo.setText(
                String.format(mContext.getString(R.string.exercise_set_two),
                        String.valueOf(setTwo.getWeight()),
                        String.valueOf(setTwo.getRepGoal())));

        holder.setThree.setText(
                String.format(mContext.getString(R.string.exercise_set_three),
                        String.valueOf(setThree.getWeight()),
                        String.valueOf(setThree.getRepGoal()) + plusSet));

        if (shouldShowRepsToBeat) {
            holder.repsToBeat.setText(
                    String.format(mContext.getString(R.string.reps_to_beat),
                            String.valueOf(repsToBeat)));
            holder.repsToBeat.setVisibility(View.VISIBLE);
        } else {
            holder.repsToBeat.setVisibility(View.GONE);
        }

        setOnClick(holder.setOne, setOne);
        setOnClick(holder.setTwo, setTwo);
        setOnClick(holder.setThree, setThree);

        holder.title.setText(getSetTypeString(setType, false));

        if (allSetsComplete(sets)) {
            holder.title.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    mContext.getResources().getDrawable(R.drawable.ic_check_black_24dp),
                    null);
        } else {
            holder.title.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        setPaint(holder.setOne, setOne);
        setPaint(holder.setTwo, setTwo);
        setPaint(holder.setThree, setThree);

        return convertView;
    }

    /**
     * Returns true if the 'reps to beat' label should be shown
     */
    private boolean shouldShowRepsToBeat(SetType setType, MainExercise mainExercise) {
        return setType == SetType.REGULAR && mainExercise.getRepsToBeat() != -1;
    }

    /**
     * Returns true if all sets are complete.
     */
    private boolean allSetsComplete(List<ExerciseSet> sets) {
        for (ExerciseSet set : sets) {
            if (!set.isComplete() && !set.isWon()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Set on click listeners for text views.
     */
    private void setOnClick(final TextView textView, final ExerciseSet set) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (set.getType().equals(SetType.PLUS_SET)) {
                    mActionListener.onActionTaken(Action.SET_REPS);
                    setPaint(textView, set);
                } else {
                    toggleCompletion(textView, set);
                }
            }
        });
    }

    /**
     * Toggles the completion of the set.
     */
    private void toggleCompletion(TextView text, ExerciseSet set) {
        set.toggleCompletion();
        setPaint(text, set);
    }

    /**
     * Sets the set paint of the text view depending on if it's complete or not.
     */
    private void setPaint(TextView text, ExerciseSet set) {
        if (set.isWon() || set.isComplete()) {
            text.setPaintFlags(
                    text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            text.setPaintFlags(
                    text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        notifyDataSetChanged();
    }

    /**
     * Returns the exercise sets based on an index.
     */
    private List<ExerciseSet> getSetsByIndex(int index) {
        return mMainExercise.getSetGroups().get(index).getSets();
    }

    /**
     * Returns the SetType based on an index.
     */
    private SetType getTypeByIndex(int index) {
        return mMainExercise.getSetGroups().get(index).getSetType();
    }

    /**
     * Returns the reps to beat for a new PR
     */
    private int getRepsToBeat() {
        return mMainExercise.getRepsToBeat();
    }

    /**
     * Returns the set type string based on set type and if it's displayed as a short.
     */
    private String getSetTypeString(SetType type, boolean shortType) {
        switch (type) {
            case WARM_UP:
                return shortType
                        ? mContext.getString(R.string.set_type_warmup_short)
                        : mContext.getString(R.string.set_type_warmup);
            case PLUS_SET:
            case REGULAR:
            default:
                return shortType
                        ? mContext.getString(R.string.set_type_main_short)
                        : mContext.getString(R.string.set_type_main);
        }
    }

    /**
     * Returns the plate break down.
     */
    private String getPlateRecommendations(double weight) {
        if (!PreferenceUtil.getBoolean(mContext, PreferenceUtil.KEY_SHOW_PLATE_RECOMMENDATIONS, true)) {
            return "";
        }
        DecimalFormat plateFormat = new DecimalFormat("0.#");
        double[] plates = {45, 25, 10, 5, 2.5};
        double side = (weight-45)/2;
        List<String> recommendation = new ArrayList<>();
        for (double p : plates){
            while (side >= p) {
                recommendation.add(plateFormat.format(p));
                side -= p;
            }
        }
        return "  [" + TextUtils.join(",", recommendation) + "]";
    }

    /**
     * ViewHolder to increase performance.
     */
    private static class ViewHolder {
        public TextView title;
        public TextView setOne;
        public TextView setTwo;
        public TextView setThree;
        public ImageView imageView;
        public TextDrawable textDrawable;
        public TextView repsToBeat;
    }
}
