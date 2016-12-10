package com.example.matt.dice;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.matt.dice.R.id.d10_value;
import static com.example.matt.dice.R.id.d12_value;
import static com.example.matt.dice.R.id.d20_value;
import static com.example.matt.dice.R.id.d4_value;
import static com.example.matt.dice.R.id.d6_value;
import static com.example.matt.dice.R.id.d8_value;
import static com.example.matt.dice.R.id.result;
import static com.example.matt.dice.R.id.static_value;
import static com.example.matt.dice.R.id.target_value;

public class PathfinderDice extends AppCompatActivity implements View.OnClickListener {

    private State state;

    public class State {
        int d4, d6, d8, d10, d12, d20;
        int staticBonus;
        int target;

        public State() {
            d4 = d6 = d8 = d10 = d12 = d20 = 0;
            staticBonus = 0;
            target = 0;
        }

        private boolean cannotWin() {
            int max = 4*d4 + 6*d6 + 8*d8 + 10*d10 + 12*d12 + 20*d20;
            return max + staticBonus < target;
        }

        private boolean cannotLose() {
            int min = d4 + d6 + d8 + d10 + d12 + d20;
            return min + staticBonus >= target;
        }

        private List<Integer> getEachDieMax() {
            List<Integer> result = new ArrayList<Integer>();
            for (int d = 0; d < d4; d++) { result.add(4); }
            for (int d = 0; d < d6; d++) { result.add(6); }
            for (int d = 0; d < d8; d++) { result.add(8); }
            for (int d = 0; d < d10; d++) { result.add(10); }
            for (int d = 0; d < d12; d++) { result.add(12); }
            for (int d = 0; d < d20; d++) { result.add(20); }
            return result;
        }

        private Map<Integer, Integer> rollAdditionalDie(Map<Integer, Integer> current, int maxValue) {
            Map<Integer, Integer> result = new HashMap<>();
            for (Map.Entry<Integer, Integer> e : current.entrySet()) {
                for (int i = 1; i <= maxValue; i++) {
                    int roll = e.getKey() + i;
                    Integer old = result.get(roll);
                    Integer updated = old != null ? old + 1 : 1;
                    result.put(roll, updated);
                }
            }
            return result;
        }


        private String calculateNonTrivial() {
            Map<Integer, Integer> results = new HashMap<Integer, Integer>();

            List<Integer> dice = getEachDieMax();
            Integer firstDie = dice.remove(0);
            for (int i = 1; i <= firstDie; i++) {
                results.put(i, 1);
            }

            for (Integer die : dice) {
                results = rollAdditionalDie(results, die);
            }

            int successCount = 0;
            int failCount = 0;
            for (Map.Entry<Integer, Integer> e : results.entrySet()) {
                if (e.getKey() + staticBonus >= target) {
                    successCount += e.getValue();
                } else {
                    failCount += e.getValue();
                }
            }

            return String.format("%.0f%%", (100.0 * successCount) / (successCount + failCount));
        }

        public String calculate() {
            if (cannotWin()) {
                return "Impossible";
            } else if (cannotLose()) {
                return "Automatic";
            } else {
                return calculateNonTrivial();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.d4_minus: this.state.d4 = Math.max(0, this.state.d4 - 1); break;
            case R.id.d6_minus: this.state.d6 = Math.max(0, this.state.d6 - 1); break;
            case R.id.d8_minus: this.state.d8 = Math.max(0, this.state.d8 - 1); break;
            case R.id.d10_minus: this.state.d10 = Math.max(0, this.state.d10 - 1); break;
            case R.id.d12_minus: this.state.d12 = Math.max(0, this.state.d12 - 1); break;
            case R.id.d20_minus: this.state.d20 = Math.max(0, this.state.d20 - 1); break;
            case R.id.target_minus: this.state.target = Math.max(0, this.state.target - 1); break;
            // Can go negative
            case R.id.static_minus: this.state.staticBonus--; break;

            case R.id.d4_plus: this.state.d4++; break;
            case R.id.d6_plus: this.state.d6++; break;
            case R.id.d8_plus: this.state.d8++; break;
            case R.id.d10_plus: this.state.d10++; break;
            case R.id.d12_plus: this.state.d12++; break;
            case R.id.d20_plus: this.state.d20++; break;
            case R.id.target_plus: this.state.target++; break;
            case R.id.static_plus: this.state.staticBonus++; break;

            case R.id.go:
                String calculation = this.state.calculate();
                ((TextView)this.findViewById(result)).setText(calculation);
                break;
        }
        updateDisplay();
    }

    public void updateDisplay() {
        ((TextView)this.findViewById(d4_value)).setText(Integer.toString(this.state.d4));
        ((TextView)this.findViewById(d6_value)).setText(Integer.toString(this.state.d6));
        ((TextView)this.findViewById(d8_value)).setText(Integer.toString(this.state.d8));
        ((TextView)this.findViewById(d10_value)).setText(Integer.toString(this.state.d10));
        ((TextView)this.findViewById(d12_value)).setText(Integer.toString(this.state.d12));
        ((TextView)this.findViewById(d20_value)).setText(Integer.toString(this.state.d20));
        ((TextView)this.findViewById(target_value)).setText(Integer.toString(this.state.target));
        ((TextView)this.findViewById(static_value)).setText(Integer.toString(this.state.staticBonus));
    }

    public void calculate() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.state = new State();
        this.setTitle("Pathfinder Dice");

        ((Button)findViewById(R.id.d4_minus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d4_plus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d6_minus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d6_plus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d8_minus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d8_plus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d10_minus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d10_plus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d12_minus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d12_plus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d20_minus)).setOnClickListener(this);
        ((Button)findViewById(R.id.d20_plus)).setOnClickListener(this);
        ((Button)findViewById(R.id.target_minus)).setOnClickListener(this);
        ((Button)findViewById(R.id.target_plus)).setOnClickListener(this);
        ((Button)findViewById(R.id.static_minus)).setOnClickListener(this);
        ((Button)findViewById(R.id.static_plus)).setOnClickListener(this);
        ((Button)findViewById(R.id.go)).setOnClickListener(this);
    }
}
