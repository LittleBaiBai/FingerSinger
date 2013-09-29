package com.game.fingersinger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class MainActivity extends Activity {

	private String dirPath;
	private String fileName = "";
	private Dialog onSaveDialog;

	private DrawLines drawView;
	private ScaleView tempoBar;

	private ImageButton menuBtn, colorBtn, undoBtn;
	private ImageView pointer;
	private EditText edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��ȥ��������Ӧ�ó�������֣�  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //��ȥ״̬������(��ص�ͼ���һ�����β���)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //�趨��������Ϊý������,����ͣ���ŵ�ʱ����������Ͳ�����Ĭ�ϵ�������������

        this.setContentView(R.layout.loading);
		Declare.drawSoundManager.initSounds(getBaseContext(), 1);
		Declare.playSoundManager.initSounds(getBaseContext(), 5);
        getDirPath();   
        initDeclare();
		initView(); 
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	void initDeclare() {
		Declare.menu_status = 1;
		Declare.color_status = 0;
		
		Declare.screen_width = getWindowManager().getDefaultDisplay().getWidth();
		Declare.screen_height = getWindowManager().getDefaultDisplay().getHeight();
		
		Declare.button_menu_vertical = (float) (Declare.screen_height * 70 / 480);
		Declare.button_menu_horizontal = (float) (Declare.screen_width * 70 / 800);
		Declare.button_color_vertical = (float) (Declare.screen_height * 45 / 480);
		Declare.button_color_horizontal = (float) (Declare.screen_width * 73 / 800);
		Declare.button_undo_vertical = (float) (Declare.screen_height * 41 / 480);
		Declare.button_undo_horizontal = (float) (Declare.screen_width * 62 / 800);
		Declare.scale_start_x_y = (float) (Declare.screen_height * 11 / 480);
		Declare.scale_length_vertical = (float) (Declare.screen_height * 28 / 480);
		Declare.pointer_pressed = (float) (Declare.screen_width * 49 / 800);
		Declare.pointer_unpress = (float) (Declare.screen_width * 42 / 800);
		Declare.pointer_stick = (float) (Declare.screen_width * 17 / 800);
		Declare.pointer_dx = (float) (Declare.screen_width * 9 / 800);
		Declare.note_inner_dist = (float) (Declare.screen_height * 20 / 480);
		
		Declare.colors[0] = getResources().getColor(R.color.green); 
		Declare.colors[1] = getResources().getColor(R.color.yellow);
		Declare.colors[2] = getResources().getColor(R.color.red);
		Declare.colors[3] = getResources().getColor(R.color.blue);
		Declare.colors[4] = getResources().getColor(R.color.purple);
	}
	
	void initView() {
		
		setContentView(R.layout.activity_main);
		//LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout tempoBarLayout = (LinearLayout) findViewById(R.id.tempo_bar);
	    tempoBar = new ScaleView(this);
	    //tempoBar.setBackgroundColor(getResources().getColor(R.color.blue));
	    tempoBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
	    tempoBarLayout.addView(tempoBar);    
	 //   tempoBar.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_eraser));   

		LinearLayout drawLayout = (LinearLayout) findViewById(R.id.view_draw);
		//drawLayout.setLayoutParams(new LayoutParams())
		drawView = new DrawLines(this);
		drawView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));  
		drawLayout.addView(drawView);

	    LinearLayout voiceBarLayout = (LinearLayout) findViewById(R.id.voice_bar);
	    voiceBarLayout.setVisibility(View.GONE);
	    
		menuBtn = (ImageButton) findViewById(R.id.button_menu);
	    colorBtn = (ImageButton) findViewById(R.id.button_color);
		undoBtn = (ImageButton) findViewById(R.id.button_undo);
		pointer = (ImageView)findViewById(R.id.pointer);
		
		menuBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) {  
	            Log.v("Button","Click on Menu");
	            if(Declare.menu_status == 1){
	            	menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_eraser));
	            	Declare.menu_status = 2;
					Toast.makeText(MainActivity.this, R.string.prompt_status_eraser, Toast.LENGTH_SHORT).show();
				}
				else if (Declare.menu_status == 2) {
					menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_voice));
					Declare.menu_status = 3;
					Toast.makeText(MainActivity.this, R.string.prompt_status_voice, Toast.LENGTH_SHORT).show();

				    LinearLayout voiceBarLayout = (LinearLayout) findViewById(R.id.voice_bar);
				    voiceBarLayout.setVisibility(View.VISIBLE);
				    
				    changeVoice();
				}
				else {	//�ȿ�����menu_status=3��������, �ֿ���menu_status=4�����ţ�
					LinearLayout voiceBarLayout = (LinearLayout) findViewById(R.id.voice_bar);
				    voiceBarLayout.setVisibility(View.GONE);
					menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_draw));
					Declare.menu_status = 1;
					undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_undo));
					Toast.makeText(MainActivity.this, R.string.prompt_status_draw, Toast.LENGTH_SHORT).show();
				}	
				
	       }

			private void changeVoice() {
				// TODO Auto-generated method stub
				SeekBar seek1 = (SeekBar)findViewById(R.id.seekBar1);
				SeekBar seek2 = (SeekBar)findViewById(R.id.seekBar2);
				SeekBar seek3 = (SeekBar)findViewById(R.id.seekBar3);
				SeekBar seek4 = (SeekBar)findViewById(R.id.seekBar4);
				SeekBar seek5 = (SeekBar)findViewById(R.id.seekBar5);
				
				seek1.setOnSeekBarChangeListener(new ProgressSeekListener(seek1, 1));
				seek2.setOnSeekBarChangeListener(new ProgressSeekListener(seek2, 2));
				seek3.setOnSeekBarChangeListener(new ProgressSeekListener(seek3, 3));
				seek4.setOnSeekBarChangeListener(new ProgressSeekListener(seek4, 4));
				seek5.setOnSeekBarChangeListener(new ProgressSeekListener(seek5, 5));
			}
	            
	    }); 
		
		colorBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) {  
	            Log.v("Button","Click on Color");
	            if (Declare.color_status == 0) {
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_yellow));
					Declare.color_status = 1;
	            }
	            else if(Declare.color_status == 1){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_red));
					Declare.color_status = 2;
				}
	            else if (Declare.color_status == 2){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_blue));
					Declare.color_status = 3;
				}
	            else if (Declare.color_status == 3){
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_purple));
					Declare.color_status = 4;
				}
				else {
	            	colorBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_color_green));
					Declare.color_status = 0;
				}	
	        }    
	    }); 
		
		undoBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) {  
	            Log.v("Button","Click on Undo");
	            if (Declare.menu_status == 4) {
	            	//��ͣ
	            	undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_resume));
	            	Declare.menu_status = 5;
	            }
	            else if (Declare.menu_status == 5) {
	            	//����
	            	undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
	            	Declare.menu_status = 4;
	            	playSong(Declare.pointerInMelody);
	            }
	            else {
	            	drawView.undo();	            	
	            }
	        }    
	    }); 

		pointer.setOnTouchListener(new PointerListener(this, pointer));
		
	} 

	private void getDirPath() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			this.dirPath = Environment.getExternalStorageDirectory().getPath() + "/FingerSinger/history/";
		}
		else 
			this.dirPath = Environment.getRootDirectory().getPath() + "/FingerSinger/history/";
	}	
	
	public boolean onOptionsItemSelected(MenuItem item) {  
	
		LinearLayout voiceBarLayout = (LinearLayout) findViewById(R.id.voice_bar);
	    voiceBarLayout.setVisibility(View.GONE);
	    
		// �ڴ�˵��һ�£�Menu�൱��һ����������MenuItem�൱�����������ɵĶ��� 
		switch(item.getItemId()) { 
		case R.id.action_playCurrent: 
			menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_stop));
			undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
			Toast.makeText(MainActivity.this, R.string.prompt_status_eraser, Toast.LENGTH_SHORT).show();
			Declare.menu_status = 4;
			playSong(Declare.pointerInMelody);
			
			break; 
		case R.id.action_playWhole: 
			menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_stop));
			undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
        	Declare.menu_status = 4;
        	Log.v("playSong", "main,here");
        	playSong(0);
			break; 
		case R.id.action_save: 
			if (fileName == "") {
				//edit = (EditText)findViewById(R.id.name_editor);
				edit = new EditText(MainActivity.this);
				edit.setSingleLine();
				edit.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_ACTION_DONE); 
			
				//�����û�������������һ����
				onSaveDialog = new AlertDialog.Builder(MainActivity.this).setTitle("����")
					.setMessage("��һ�����׵����ְɣ���Ҫ������.���� ����").setView(edit)
					//���ð�ť
					.setPositiveButton("����", new DialogInterface.OnClickListener(){  
						public void onClick(DialogInterface dialog, int which) {
							fileName = edit.getText().toString();
							//Log.v("debuga", "fileName: " + fileName);
							
							if (new File(dirPath + "/" + fileName + ".psong").exists()) {
								Log.v("debuga", "file exists()");
								AlertDialog temp = new AlertDialog.Builder(MainActivity.this)
									.setTitle("����ʧ��").setMessage("������")
									.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog, int which) {
											//�����κβ���
											edit.setVisibility(View.GONE);
										}
									}).create();
								temp.show();
							}
							else {
								//����Ӧ��������
								try {
									onSave();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								fileName = "";
								//��ջ���Ҫ�����£���
								Log.v("clearCanvas", "inside");
								drawView.clearCanvas();
															
							}
						}  
					})
					.setCancelable(true).create();
				Log.v("clearCanvas", "outof");
				onSaveDialog.show();
			}

			else {
				//����Ӧ��������
				try {
					onSave();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fileName = "";
				//��ջ���Ҫ�����£���
				Log.v("clearCanvas", "else");
				drawView.clearCanvas();
				
			}
			break; 
		case R.id.action_history:
			if (Declare.isSaved == false) {
				Dialog isSaveDialog = new AlertDialog.Builder(MainActivity.this).setMessage("��ǰ��Ŀ�Ƿ񱣴棿")
					//���ð�ť
					.setPositiveButton("����", new DialogInterface.OnClickListener(){ 
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							onSaveDialog.show();
							Declare.isSaved = true;
							//���ļ�ϵͳ���ҳ��ļ�
							showFileList();
						}  
					})
					.setNegativeButton("������", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Declare.isSaved = true;
							//���ļ�ϵͳ���ҳ��ļ�
							showFileList();
						}
					})
					.setCancelable(true).create();
				isSaveDialog.show();
			}
			else {
				//���ļ�ϵͳ���ҳ��ļ�
				showFileList();
			}
			break; 
		case R.id.action_import: 
			Toast.makeText(MainActivity.this, "�˹�����δ��ɣ������ڴ�", Toast.LENGTH_SHORT).show();
			break; 
		case R.id.action_export:
			Toast.makeText(MainActivity.this, "�˹�����δ��ɣ������ڴ�", Toast.LENGTH_SHORT).show();
			break;
		default:
			Log.v("debuga", "menu_id: " + item.getItemId());
			break;
		}  
		return true;  
	}  

	protected void loadFromHistory() {		
		try{
			//���ļ��лָ�
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(dirPath + "/" + fileName + ".psong"));
			Declare.melody = (Melody[])in.readObject();
			in.close();
			
			//�ػ�����
			drawView.clearCanvas();
			drawView.reDraw();
			
		}
		catch(Exception e){
	       e.printStackTrace();
        }
		
	}

	@SuppressLint("SimpleDateFormat")
	private void showFileList() {
		ListView listview = (ListView)this.findViewById(R.id.list_view);
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		listview.setBackgroundColor(Color.GRAY);
		File f = new File(this.dirPath); //strPathΪ·��
		//File f = new File(Environment.getRootDirectory().getPath()); //strPathΪ·��

		//Log.v("debuga", f.getPath());
		if (!(f.exists())) {
			Log.v("debuga", "not exists");
			Dialog dialog = new AlertDialog.Builder(MainActivity.this)  
				//���öԻ���Ҫ��ʾ����Ϣ  
				.setMessage("����ʷ��Ŀ")  
				//���ð�ť
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){  
					public void onClick(DialogInterface dialog, int which) {  
						//����ʲô�����ø�
					}  
				}).create();
			dialog.show();
		}
		else {
			String[] file_list = f.list(); //String[] 
			SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
			Log.v("debuga", "file_list:" + f.exists());
			for (int i = 0; i < file_list.length; i++) {
				if (file_list[i].endsWith(".psong")) {
					HashMap<String, String> info = new HashMap<String, String>();
					Log.v("debuga", "aaa:" + dateformat1.format(((new File(dirPath + "/" + file_list[i])).lastModified())) + " length:" + file_list[i]);
					info.put("name", file_list[i].substring(0, file_list[i].length() - 6));
					info.put("time", dateformat1.format(((new File(dirPath + "/" + file_list[i])).lastModified())));
					data.add(info);
				}
			}
		}
		
		SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.item_menu, 
				new String[] {"name", "time"}, new int[] {R.id.name, R.id.time});
		listview.setAdapter(adapter);
		listview.bringToFront();
		listview.setVisibility(View.VISIBLE);
		
		//Log.v("debuga", listview.toString());
		/*listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Toast.makeText(MainActivity.this, "LongClick", 200).show();
				return false;
			}
		});
		*/
	
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ListView lv = (ListView)arg0;
				@SuppressWarnings("unchecked")
				HashMap<String, String> tem = (HashMap<String, String>)lv.getItemAtPosition(arg2);
				fileName = tem.get("name");
				//Toast.makeText(MainActivity.this, "Click" + fileName, 200).show();
				loadFromHistory();
				lv.setVisibility(View.GONE);
			}
		});
		
	}

	private void onSave() throws IOException {
		File f = new File(this.dirPath); //strPathΪ·��
		if (!f.exists()) {
			f.mkdirs();	//�����ļ��� 
			Log.v("debug", "mkdir~");
			Log.v("debug", "f.exists(): " + f.getPath() + " " + f.exists());
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dirPath + "/" + fileName + ".psong"));
		out.writeObject(Declare.melody);
		out.close();
		Declare.isSaved = true;
		Toast.makeText(MainActivity.this, "Already Saved", Toast.LENGTH_SHORT).show();
	}
	
	public void playSong(int start) {
		// TODO Auto-generated method stub
		int end = 0;
		for (int i = 0; i < 5; i++) {
			Log.v("playSong", "color: " + i + " size:" + Declare.melody[i].stops.size());
			if (Declare.melody[i].stops.size() == 0) continue;
			int temp = Declare.melody[i].stops.get(Declare.melody[i].stops.size() - 1) - 1;
			if (temp > end) {
				end = temp;
			}
		}
		Log.v("playSong", "before for loop��" + start + "/" + end);
		for (int i = start; i < end; i++) {
			for (int j = 0; j < 5; j++) {
				if (Declare.melody[j].notes.isEmpty()) continue;
				if (Declare.melody[j].stops.get(Declare.melody[j].stops.size() - 1) - 1 < i) continue;
				int note = Declare.melody[j].notes.get(i);
				Log.v("playSong", start + "/" + end + " note: " + note);
				if (Declare.menu_status == 5) {
					break;
				}
				if (note == 0) {
					Declare.playSoundManager.playSound(0, Declare.melody[j].voice);
				}
				else {
					Declare.playSoundManager.playSound(Declare.getIndexOfSound(note) + 22 * j, Declare.melody[j].voice);
				}
			}	
			if (Declare.menu_status == 5) {
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_draw));
		undoBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_undo));
		Declare.menu_status = 1;
		Toast.makeText(MainActivity.this, R.string.prompt_stop_play, Toast.LENGTH_SHORT).show();
		
	}

	
}
