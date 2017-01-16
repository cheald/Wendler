package se.johan.wendler.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object describing a set used for exercises.
 */
public class ExerciseSet implements Parcelable {

    private final SetType mSetType;
    private double mWeight = 0.0;
    private final int mSetGoal;
    private final int mRepGoal;
    private int mProgress;
    private boolean mIsComplete;

    /**
     * Constructor used for last sets on the main exercise.
     */
    public ExerciseSet(SetType type, double weight, int setGoal, int repGoal, int progress, boolean isComplete) {
        mSetType = type;
        mWeight = weight;
        mSetGoal = setGoal;
        mRepGoal = repGoal;
        mProgress = progress;
        mIsComplete = isComplete;
    }

    /**
     * Constructor used for last sets on the main exercise.
     */
    public ExerciseSet(SetType type, double weight, int setGoal, int repGoal, int progress) {
        mSetType = type;
        mWeight = weight;
        mSetGoal = setGoal;
        mRepGoal = repGoal;
        mProgress = progress;
        mIsComplete = mProgress >= mSetGoal;
    }

    /**
     * Return the type of set.
     */
    public SetType getType() {
        return mSetType;
    }

    /**
     * Return the weight.
     */
    public double getWeight() {
        return mWeight;
    }

    /**
     * Return the set goal.
     */
    public int getSetGoal() {
        return mSetGoal;
    }

    /**
     * Return the rep goal.
     */
    public int getRepGoal() {
        return mRepGoal;
    }

    /**
     * Return if the set is complete.
     */
    public boolean isComplete() {
        return mIsComplete;
    }

    /**
     * Return the progress of the set.
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * Set the progress of the set.
     */
    public void setProgress(int progress) {
        mProgress = progress;
    }

    /**
     * Toggle the completion status of the set.
     */
    public void toggleCompletion() {
        mIsComplete = !mIsComplete;
    }

    public boolean isWon() {
        return mProgress >= mRepGoal;
    }

    /**
     * Used for parcelable.
     */
    protected ExerciseSet(Parcel in) {
        mSetType = (SetType) in.readValue(SetType.class.getClassLoader());
        mWeight = in.readDouble();
        mSetGoal = in.readInt();
        mRepGoal = in.readInt();
        mProgress = in.readInt();
        mIsComplete = in.readInt() == 1;
    }

    /**
     * Used for parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Used for parcelable.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mSetType);
        dest.writeDouble(mWeight);
        dest.writeInt(mSetGoal);
        dest.writeInt(mRepGoal);
        dest.writeInt(mProgress);
        dest.writeInt(mIsComplete ? 1 : 0);
    }

    /**
     * Used for creating a parcelable.
     */
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ExerciseSet>
            CREATOR = new Parcelable.Creator<ExerciseSet>() {
        @Override
        public ExerciseSet createFromParcel(Parcel in) {
            return new ExerciseSet(in);
        }

        @Override
        public ExerciseSet[] newArray(int size) {
            return new ExerciseSet[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExerciseSet) {
            ExerciseSet other = (ExerciseSet) o;
            return other.getType().equals(mSetType)
                    && other.getWeight() == mWeight
                    && other.getSetGoal() == mSetGoal
                    && other.getRepGoal() == mRepGoal
                    && other.isComplete() == mIsComplete;
        }
        return false;
    }
}