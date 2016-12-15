package cc.metapro.openct.gradelist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cc.metapro.openct.R;
import cc.metapro.openct.data.GradeInfo;

/**
 * Created by jeffrey on 16/12/2.
 */

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private List<GradeInfo> mGradeInfos;

    private Context mContext;

    public GradeAdapter(Context context) {
        mContext = context;
        mGradeInfos = new ArrayList<>(0);
    }

    @Override
    public GradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GradeViewHolder holder, int position) {
        final GradeInfo g = mGradeInfos.get(position);
        holder.setClassName(g.getClassName());
        holder.setGradeSummary(g.getGradeSummary());
        holder.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
                ab.setMessage(g.toFullString());
                ab.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGradeInfos.size();
    }

    public void setNewGradeInfos(List<GradeInfo> gradeInfos) {
        if (gradeInfos == null || gradeInfos.size() == 0) {
            mGradeInfos = new ArrayList<>(0);
        } else {
            mGradeInfos = gradeInfos;
        }
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView mClassName, mGradeSummary;

        public GradeViewHolder(View itemView) {
            super(itemView);
            mClassName = (AppCompatTextView) itemView.findViewById(R.id.grade_class_name);
            mGradeSummary = (AppCompatTextView) itemView.findViewById(R.id.grade_level);
        }

        public void setClassName(String name) {
            mClassName.setText(name);
        }

        public void setGradeSummary(String grade) {
            mGradeSummary.setText("总评成绩  " + grade);
            try {
                int i = Integer.parseInt(grade);
                if (i < 60) {
                    mGradeSummary.setTextColor(Color.parseColor("#FF4081"));
                } else {
                    mGradeSummary.setTextColor(Color.parseColor("#3F51B5"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setListener(View.OnClickListener l) {
            mClassName.setOnClickListener(l);
            mGradeSummary.setOnClickListener(l);
        }
    }
}