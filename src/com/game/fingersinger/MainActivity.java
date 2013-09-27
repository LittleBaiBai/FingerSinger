package com.game.fingersinger;

import org.apache.http.util.EncodingUtils;

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
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	private String dirPath;
	private String fileName = "";
	private Dialog onSaveDialog;
	private View drawView;
	private ImageView musicBar, tempoBar;
	private ImageButton menuBtn, colorBtn, undoBtn;
	private EditText edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//隐去标题栏（应用程序的名字）  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐去状态栏部分(电池等图标和一切修饰部分)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设定调整音量为媒体音量,当暂停播放的时候调整音量就不会再默认调整铃声音量了

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
		
		Declare.screen_width = getWindowManager().getDefaultDisplay().getWidth();
		Declare.screen_height = getWindowManager().getDefaultDisplay().getHeight();
		
		Declare.button_menu_vertical = (float) (Declare.screen_height/6.85714286);
		Declare.button_menu_horizontal = (float) (Declare.screen_width/11.4285714);
		Declare.button_color_vertical = (float) (Declare.screen_height/12.3076923);
		Declare.button_color_horizontal = (float) (Declare.screen_width/10.9589041);
		Declare.button_undo_vertical = (float) (Declare.screen_height/9.75609756);
		Declare.button_undo_horizontal = (float) (Declare.screen_width/12.9032258);
		Declare.scale_start_x_y = (float) (Declare.screen_height/14.1176471);
		Declare.scale_start_y_x = (float) (Declare.screen_width/200);
		Declare.scale_length_horizontal = (float) (Declare.screen_width/28.5714286);
		Declare.note_top_dist = (float) (Declare.screen_height/19.2);
		Declare.note_button_dist = (float) (Declare.screen_height/1.24352332);
		Declare.note_inner_dist = (float) (Declare.screen_height/25.2631579);
		
		Declare.colors[0] = getResources().getColor(R.color.green); 
		Declare.colors[1] = getResources().getColor(R.color.yellow);
		Declare.colors[2] = getResources().getColor(R.color.red);
		Declare.colors[3] = getResources().getColor(R.color.blue);
		Declare.colors[4] = getResources().getColor(R.color.purple);
	}
	
	void initView() {
		
		setContentView(R.layout.activity_main);
		//LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout drawLayout = (LinearLayout) findViewById(R.id.view_draw);
		//drawLayout.setLayoutParams(new LayoutParams())
		drawView = new DrawLines(this);
		drawView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));  
		drawLayout.addView(drawView);
	    
	    LinearLayout tempoBarLayout = (LinearLayout) findViewById(R.id.tempo_bar);
	    tempoBar = new ImageView(this);
	    tempoBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
	    tempoBarLayout.addView(tempoBar);    
	 //   tempoBar.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_eraser));   
	    
	    LinearLayout musicBarLayout = (LinearLayout) findViewById(R.id.music_bar);
	    musicBar = new ImageView(this);
	    musicBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
	    musicBarLayout.addView(musicBar);    
	//     musicBar.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_eraser));   
		
		menuBtn = (ImageButton) findViewById(R.id.button_menu);
	    colorBtn = (ImageButton) findViewById(R.id.button_color);
		undoBtn = (ImageButton) findViewById(R.id.button_undo);
		
	    //设置按钮的监听 		
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
				}
				else {	//既可能是menu_status=3（音量）, 又可能menu_status=4（播放）
					menuBtn.setImageDrawable(getResources().getDrawable(R.drawable.button_status_draw));
					Declare.menu_status = 1;
					Toast.makeText(MainActivity.this, R.string.prompt_status_draw, Toast.LENGTH_SHORT).show();
				}	
				
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
					Declare.color_status = 1;
				}	
	        }    
	    }); 
		
		undoBtn.setOnClickListener(new OnClickListener() {  
	        
	        public void onClick(View v) {  
	            Log.v("Button","Click on Undo");
	            	
	        }    
	    }); 
	} 

	private void getDirPath() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			this.dirPath = Environment.getExternalStorageDirectory().getPath() + "/PhotoSong/history/";
		}
		else 
			this.dirPath = Environment.getRootDirectory().getPath() + "/PhotoSong/history/";
	}	
	
	public boolean onOptionsItemSelected(MenuItem item) {  
	
		// 在此说明一下，Menu相当于一个容器，而MenuItem相当于容器中容纳的东西 
		switch(item.getItemId()) { 
		case R.id.action_playCurrent: 
			setTitle("播放当前"); 
			break; 
		case R.id.action_playWhole: 
			setTitle("播放整曲"); 
			break; 
		case R.id.action_save: 
			if (fileName == "") {
				//edit = (EditText)findViewById(R.id.name_editor);
				edit = new EditText(MainActivity.this);
				edit.setSingleLine();
				edit.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_ACTION_DONE); 
				//edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
				//提醒用户做保存的事情的一个框
				onSaveDialog = new AlertDialog.Builder(MainActivity.this).setTitle("保存")
						.setMessage("想一个靠谱的名字吧，不要包括“.”＝ ＝、").setView(edit)
					//设置按钮
					.setPositiveButton("保存", new DialogInterface.OnClickListener(){  
						public void onClick(DialogInterface dialog, int which) {
							fileName = edit.getText().toString();
							Log.v("debuga", "fileName: " + fileName);
							
							if (new File(dirPath + "/" + fileName + ".psong").exists()) {
								Log.v("debuga", "exists()");
								AlertDialog temp = new AlertDialog.Builder(MainActivity.this)
									.setTitle("保存失败").setMessage("有重名")
									.setPositiveButton("确定", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog, int which) {
											//不做任何操作
											edit.setVisibility(View.GONE);
										}
									}).create();
								temp.show();
							}
							else {
								//保存应该做的事
								try {
									onSave();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								fileName = "";
								//清空画布要做的事！！
								
								
								
								
							}
						}  
					})
					.setCancelable(true).create();
				onSaveDialog.show();
			}

			else {
				//保存应该做的事
				try {
					onSave();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fileName = "";
				//清空画布要做的事！！
				
				
			}
			break; 
		case R.id.action_history:
			if (Declare.isSaved == false) {
				Dialog isSaveDialog = new AlertDialog.Builder(MainActivity.this).setMessage("当前曲目是否保存？")
					//设置按钮
					.setPositiveButton("保存", new DialogInterface.OnClickListener(){ 
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							onSaveDialog.show();
							Declare.isSaved = true;
							loadFromHistory();
						}  
					})
					.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Declare.isSaved = true;
							loadFromHistory();
						}
					})
					.setCancelable(true).create();
				isSaveDialog.show();
			}
			else {
				loadFromHistory();
			}
			break; 
		case R.id.action_import: 
			Toast.makeText(MainActivity.this, "此功能尚未完成", Toast.LENGTH_SHORT).show();
			break; 
		case R.id.action_export:
			Toast.makeText(MainActivity.this, "此功能尚未完成", Toast.LENGTH_SHORT).show();
			break;
		default:
			Log.v("debuga", "menu_id: " + item.getItemId());
			break;
		}  
		return true;  
	}  

	protected void loadFromHistory() {
		//调文件系统，找出文件
		showFileList();

		Log.v("debuga", "fileName: " + fileName);
	
		//从文件中恢复
		try{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(dirPath + "/" + fileName));
			Integer obj3 = (Integer) in.readObject();
			in.close();
			//重画画布
			
			
			
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
		File f = new File(this.dirPath); //strPath为路径
		//File f = new File(Environment.getRootDirectory().getPath()); //strPath为路径

		//Log.v("debuga", f.getPath());
		if (!(f.exists())) {
			Log.v("debuga", "not exists");
			Dialog dialog = new AlertDialog.Builder(MainActivity.this)  
				//设置对话框要显示的消息  
				.setMessage("无历史曲目")  
				//设置按钮
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){  
					public void onClick(DialogInterface dialog, int which) {  
						//好像什么都不用干
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
				Toast.makeText(MainActivity.this, "Click" + fileName, 200).show();
				lv.setVisibility(View.GONE);
			}
		});
		
	}

	private void onSave() throws IOException {
		File f = new File(this.dirPath); //strPath为路径
		if (!f.exists()) {
			f.mkdirs(); //建立文件夹 
			Log.v("debug", "mkdir~");
			Log.v("debug", "f.exists(): " + f.getPath() + " " + f.exists());
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dirPath + "/" + fileName + ".psong"));
		out.writeObject(3);
		out.close();
		Declare.isSaved = true;
		Toast.makeText(MainActivity.this, "Already Saved", Toast.LENGTH_SHORT).show();
	}
	
}
